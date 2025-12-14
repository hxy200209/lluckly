package cn.bugstack.domain.strategy.service.armory;

/**
 * @program: big-market
 * @description:
 * @author: hxy
 * @create: 2025-12-14 12:26
 **/

public interface IStrategyDispatch {
    Integer getRandomAwaradId(Long StrategyId);

    /*
    幸运值，保底机制
     */
    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);
}
