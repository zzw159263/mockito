package com.self;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class ArticleManagerTest {

    @Mock
    private ArticleCalculator calculator;
    @Mock
    private ArticleDatabase database;
    @Mock
    private UserProvider userProvider;

    private ArticleManager manager;

    /**
     * 使用注解简便创建mock对象
     * @see Mock
     *
     * 减少重复的模拟创建代码。
     * 使测试类更具可读性。
     * 使验证错误更容易读取，因为字段名称用于标识模拟。
     */
    @Test
    void testSomethingInJunit5(@Mock ArticleDatabase database) {

    }


}
