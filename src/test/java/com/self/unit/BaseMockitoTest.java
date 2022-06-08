package com.self.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

public class BaseMockitoTest {

    /**
     * 1、打桩
     * <p>
     * 默认情况下，对于所有返回值的方法，mock将根据情况返回null、基本类型/包装类型值或空集合。例如，0代表int/Integer, false代表boolean/Boolean。
     * 打桩可以被重写:例如，普通打桩可以进入fixture设置，但测试方法可以重写它。
     * 一旦打桩，该方法将始终返回一个打桩值，无论它被调用了多少次。
     * 最后一个打桩更为重要—当您多次使用相同的参数打桩相同的方法时。换句话说:打桩的顺序很重要，但只有在极少数情况下才有意义，例如，当打桩完全相同的方法调用时，或者有时使用参数匹配器时，等等。
     */
    @Test
    public void stubbingTest() {
        // 可以mock具体的类，而不仅仅是接口
        LinkedList mockedList = mock(LinkedList.class);

        // 打桩（stubbing）
        when(mockedList.get(0)).thenReturn("first");
        when(mockedList.get(1)).thenThrow(new RuntimeException());

        // 以下代码打印 first
        System.out.println(mockedList.get(0));

        // 以下代码抛出异常
        System.out.println(mockedList.get(1));

        // 以下代码打印 null
        System.out.println(mockedList.get(999));
    }

    /**
     * 2、参数匹配
     */
    @Test
    public void argumentMatchersTest() {
        ArrayList<String> mockedList = mock(ArrayList.class);

        // 使用内置的anyInt()参数匹配器进行打桩
        when(mockedList.get(anyInt())).thenReturn("element");

        // 以下代码打印 element
        System.out.println(mockedList.get(999));

        // 也可以使用参数匹配器校验
        verify(mockedList).get(anyInt());

        // 参数匹配器也可以写成Java 8 Lambdas
        verify(mockedList).add(argThat(someString -> someString.length() > 5));
    }

    /**
     * 3、校验确切地调用次数、至少调用x次、从未调用
     */
    @Test
    public void invocationsNumberTest() {
        ArrayList<String> mockedList = mock(ArrayList.class);

        mockedList.add("once");

        mockedList.add("twice");
        mockedList.add("twice");

        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");

        // 以下两个验证的工作完全相同—默认使用times(1)
        verify(mockedList).add("once");
        verify(mockedList, times(1)).add("once");

        // 确切地调用次数验证
        verify(mockedList, times(2)).add("twice");
        verify(mockedList, times(3)).add("three times");

        // 校验从未调用，等价于 times(0)
        verify(mockedList, never()).add("never happened");

        verify(mockedList, atMostOnce()).add("once");
        verify(mockedList, atLeastOnce()).add("three times");
        verify(mockedList, atLeast(2)).add("three times");
        verify(mockedList, atMost(5)).add("three times");
    }

    /**
     * 4、校验执行顺序
     */
    @Test
    public void inOrderTest() {
        ArrayList singleMock = mock(ArrayList.class);

        singleMock.add("was added first");
        singleMock.add("was added second");

        // 为single mock创建一个inOrder验证器
        InOrder inOrder = inOrder(singleMock);

        // 以下代码将确保首先使用"was added first"调用add，然后使用"was added second"调用add
        inOrder.verify(singleMock).add("was added first");
        inOrder.verify(singleMock).add("was added second");

        List firstMock = mock(List.class);
        List secondMock = mock(List.class);

        firstMock.add("was called first");
        secondMock.add("was called second");

        // 创建inOrder对象，传递任何需要按顺序验证的mock
        inOrder.verify(firstMock).add("was called first");
        inOrder.verify(secondMock).add("was called second");

        // 也可以将两种校验顺序混合使用
    }

    /**
     * 5、确保在模拟中不会发生交互
     */
    @Test
    public void neverHappenTest() {
        ArrayList<String> mockOne = mock(ArrayList.class);

        verify(mockOne).add("one");

        // 验证从未在mock上调用该方法
        verify(mockOne, never()).add("two");
    }

    /**
     * 6、发现多余调用
     * <p>
     * 不建议在每个测试方法中都使用verifyNoMoreInteractions()。verifyNoMoreInteractions()是来自交互测试工具包的一个方便的断言。
     * 只有在相关的时候才使用它。滥用它会导致单元测试过度指定、更不容易维护。
     */
    @Test
    public void findRedundantInvocations() {
        ArrayList mockedList = mock(ArrayList.class);

        mockedList.add("one");
        mockedList.add("two");

        verify(mockedList).add("one");

        verifyNoMoreInteractions(mockedList);
    }

    /**
     * 7、使用注解简便创建mock对象
     * @see Mock
     *
     * 减少重复的模拟创建代码。
     * 使测试类更具可读性。
     * 使验证错误更容易读取，因为字段名称用于标识模拟。
     *
     * 【注意】 该功能需要在基类或者其他位置运行如下代码：
     * MockitoAnnotations.openMocks(testClass);
     */
    @Mock
    private ArticleCalculator calculator;
    @Mock
    private ArticleDatabase database;
    @Mock
    private UserProvider userProvider;

    private ArticleManager manager;

    @Test
    void testSomethingInJunit5(@Mock ArticleDatabase database) {

    }

    /**
     * 8、打桩连续调用(迭代器风格)
     *
     * 有时我们需要打桩不同的返回值/异常相同的方法调用。典型的用例可能是模拟迭代器。
     * 最初版本的Mockito不能通过单一mock实现这个功能。
     * 例如，可以使用Iterable或简单的集合来代替迭代器。
     * 它们提供了自然的打桩方式(例如使用真正的集合)。不过，在极少数情况下，将连续调用打桩化可能会有用
     */
    @Test
    public void consecutiveCallsTest() {
        when(mock.someMethod("some arg"))
                .thenThrow(new RuntimeException())
                .thenReturn("foo");

        // 第一次调用：抛出 runtimeException
        mock.someMethod("some arg");

        // 第二次调用：打印 "foo"
        System.out.println(mock.someMethod("some arg"));

        // 任何连续调用: 同样打印 "foo"  (使用最后一个打桩的值).
        System.out.println(mock.someMethod("some arg"));

        // 连续打桩的简化版本
        when(mock.someMethod("some arg"))
                .thenReturn("one", "two", "three");

        // 注意：如果不是链式调用而是使用带有相同匹配器或参数的多个打桩，则每个打桩将覆盖前一个:
        when(mock.someMethod("some arg"))
                .thenReturn("one");
        when(mock.someMethod("some arg"))
                .thenReturn("two");
    }

    /**
     * 打桩回调
     *
     * 这是Mockito最初没有包含的另一个有争议的功能。
     * 建议简单地使用thenReturn()或thenThrow()打桩，这应该足以测试/测试驱动任何干净和简单的代码。
     * 但是，如果确实需要使用通用的Answer接口打桩，下面是一个示例:
     */
    @Test
    public void callBacksTest() {
        when(mock.someMethod(anyString())).thenAnswer(
                new Answer() {
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        Object mock = invocation.getMock();
                        return "called with arguments: " + Arrays.toString(args);
                    }
                });

        when(mock.someMethod(anyString())).thenAnswer(
                (Answer) invocation -> {
                    Object[] args = invocation.getArguments();
                    Object mock = invocation.getMock();
                    return "called with arguments: " + Arrays.toString(args);
                });

        //Following prints "called with arguments: [foo]"
        System.out.println(mock.someMethod("foo"));
    }

    /**
     * doReturn()|doThrow()| doAnswer()|doNothing()|doCallRealMethod() 系列方法
     */
    @Test
    public void stubbingFamilyMethodTest() {
        ArrayList mockedList = mock(ArrayList.class);

        /*
         * 以下将抛出 runtimeException
         * 也可以使用doThrow(), doAnswer(), doNothing(), doReturn() and doCallRealMethod()来代替when()对任何方法的相应调用。
         */
        doThrow(new RuntimeException()).when(mockedList).clear();
        mockedList.clear();
    }

    /**
     * spy
     *
     *
     */
    @Test
    public void spyingTest() {
        List<String> list = new LinkedList<>();
        List<String> spy = spy(list);

        when(spy.size()).thenReturn(100);

        spy.add("one");
        spy.add("two");

        // 打印list中第一个元素 one
        System.out.println(spy.get(0));

        // size()方法被打桩，返回100
        System.out.println(spy.size());

        verify(spy).add("one");
        verify(spy).add("two");
    }
}
