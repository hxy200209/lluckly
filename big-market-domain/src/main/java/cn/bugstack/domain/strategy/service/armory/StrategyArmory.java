package cn.bugstack.domain.strategy.service.armory;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

/**
 * @program: big-market
 * @description:策略工厂实现类,负责初始化策略
 * @author: hxy
 * @create: 2025-12-13 15:07
 **/
@Service
@Slf4j
public class StrategyArmory implements IStrategyArmory{

    @Resource
    private IStrategyRepository repository;
    @Override
    public void assembleLottryStrategy(Long strategyId) {
        //1.查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities=repository.queryStrategyAwardList(strategyId);

        //2.获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        //3.获取概率值总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //4.用1/0.0001，获取概率范围
        BigDecimal rateRange1 = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);
        int rateRange=rateRange1.intValue();

        ArrayList<Integer> strategyAwardSerchRateTables = new ArrayList<>(rateRange1.intValue());

        for (StrategyAwardEntity strategyAward:strategyAwardEntities){
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();

            //计算每个概率存放到查找表的数量
            for (int i=0;i<rateRange1.multiply(awardRate).setScale(0,RoundingMode.CEILING).intValue();i++){
                strategyAwardSerchRateTables.add(awardId);
            }
        }

        //6.乱序
        Collections.shuffle(strategyAwardSerchRateTables);

        //7.
        HashMap<Integer, Integer> shufflestrategyAwardSerchRateTables = new HashMap<>();
        for(int i=0;i<strategyAwardSerchRateTables.size();i++){
            shufflestrategyAwardSerchRateTables.put(i,strategyAwardSerchRateTables.get(i));
        }

        //8.存到redis
        repository.storeStrategyAwardSerchRateTables(strategyId,rateRange,shufflestrategyAwardSerchRateTables);
    }

    @Override
    public Integer getRandomAwaradId(Long StrategyId) {
        int rateRange=repository.getRateRange(StrategyId);
        return repository.getStrategyAwardAssemble(StrategyId,new SecureRandom().nextInt(rateRange));
    }
}
