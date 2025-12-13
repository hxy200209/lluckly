package cn.bugstack.domain.strategy.service.armory;

/**
 * @program: big-market
 * @description: 策略工厂实现
 * @author: hxy
 * @create: 2025-12-13 15:05
 **/

public interface IStrategyArmory {

    void assembleLottryStrategy(Long StrategyId);

    Integer getRandomAwaradId(Long StrategyId);
}
