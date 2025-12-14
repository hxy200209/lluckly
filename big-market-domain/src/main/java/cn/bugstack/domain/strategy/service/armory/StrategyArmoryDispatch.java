package cn.bugstack.domain.strategy.service.armory;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
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
public class StrategyArmoryDispatch implements IStrategyArmory,IStrategyDispatch{

    @Resource
    private IStrategyRepository repository;
    @Override
    public boolean assembleLottryStrategy(Long strategyId) {
        //1.查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities=repository.queryStrategyAwardList(strategyId);
        assembleLottryStrategy(String.valueOf(strategyId),strategyAwardEntities);

        //2.权重配置--使用于rule-weight权重规则配置,幸运值机制
        StrategyEntity strategyEntity=repository.querystrategyEntityByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if (null == ruleWeight) return true;

        StrategyRuleEntity strategyRuleEntity = repository.queryStrategyRule(strategyId, ruleWeight);

        if (null == strategyRuleEntity) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(), ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }
        Map<String, List<Integer>> ruleWeightValueMap = strategyRuleEntity.getRuleWeightValues();
        Set<String> keys = ruleWeightValueMap.keySet();
        for (String key : keys) {
            List<Integer> ruleWeightValues = ruleWeightValueMap.get(key);
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            strategyAwardEntitiesClone.removeIf(entity -> !ruleWeightValues.contains(entity.getAwardId()));
            assembleLottryStrategy(String.valueOf(strategyId).concat("_").concat(key), strategyAwardEntitiesClone);
        }

        return true;

    }

    private void assembleLottryStrategy(String key,List<StrategyAwardEntity> strategyAwardEntities){
        //1.获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        //2.获取概率值总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //3.用1/0.0001，获取概率范围
        BigDecimal rateRange1 = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);
        int rateRange=rateRange1.intValue();

        ArrayList<Integer> strategyAwardSerchRateTables = new ArrayList<>(rateRange1.intValue());

        for (StrategyAwardEntity strategyAward:strategyAwardEntities){
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();

            //4.计算每个概率存放到查找表的数量
            for (int i=0;i<rateRange1.multiply(awardRate).setScale(0,RoundingMode.CEILING).intValue();i++){
                strategyAwardSerchRateTables.add(awardId);
            }
        }

        //5.乱序
        Collections.shuffle(strategyAwardSerchRateTables);

        //6.
        HashMap<Integer, Integer> shufflestrategyAwardSerchRateTables = new HashMap<>();
        for(int i=0;i<strategyAwardSerchRateTables.size();i++){
            shufflestrategyAwardSerchRateTables.put(i,strategyAwardSerchRateTables.get(i));
        }

        //7.存到redis
        repository.storeStrategyAwardSerchRateTables(key,rateRange,shufflestrategyAwardSerchRateTables);
    }

    @Override
    public Integer getRandomAwaradId(Long StrategyId) {
        int rateRange=repository.getRateRange(StrategyId);
        return repository.getStrategyAwardAssemble(String.valueOf(StrategyId),new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key = String.valueOf(strategyId).concat("_").concat(ruleWeightValue);
        int rateRange = repository.getRateRange(key);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return repository.getStrategyAwardAssemble(key, new SecureRandom().nextInt(rateRange));
    }
}
