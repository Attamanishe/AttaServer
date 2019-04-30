package atta.server.comand.model;

import com.jsoniter.JsonIterator;

import java.io.Serializable;

public class CommandModel implements Serializable
{
    public int _cmd;
    public int[] _ai = new int[0];
    public double[] _ad = new double[0];
    public String[] _as = new String[0];
    public long[] _al = new long[0];
    public Object[] _am = new Object[0];

    public <T> T getModel(int index, Class<T> c)
    {
        if (_am.length < index && index > 0)
        {
            return JsonIterator.deserialize(_am[index].toString(), c);
        }
        return null;
    }
}
