package atta.utill.container;

public class Container<T1, T2>
{
    public T1 first;
    public T2 second;

    public Container(T1 first, T2 second)
    {
        this.first = first;
        this.second = second;
    }
}
