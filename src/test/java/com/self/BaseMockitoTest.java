package com.self;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

public class BaseMockitoTest {

    /**
     * 打桩
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
     * 参数匹配
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
     * 校验确切地调用次数、至少调用x次、从未调用
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
     * 校验执行顺序
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
     * 确保在模拟中不会发生交互
     */
    @Test
    public void neverHappenTest() {
        ArrayList<String> mockOne = mock(ArrayList.class);

        verify(mockOne).add("one");

        // 验证从未在mock上调用该方法
        verify(mockOne, never()).add("two");
    }

    /**
     * 发现多余调用
     *
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



}
