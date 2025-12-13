package cn.bugstack.domain.strategy.repository;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * @program: big-market
 * @description: 策略仓储接口
 * @author: hxy
 * @create: 2025-12-13 15:09
 **/

public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSerchRateTables(Long strategyId, int rateRange, HashMap<Integer, Integer> shufflestrategyAwardSerchRateTables);

    int getRateRange(Long strategyId);

    Integer getStrategyAwardAssemble(Long strategyId, int i);
}
