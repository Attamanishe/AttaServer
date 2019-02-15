package atta.server.comand.model;

import java.io.Serializable;

public class CommandModel implements Serializable
{
    public int _cmd;
    public int[] _ai = new int[0];
    public double[] _ad = new double[0];
    public String[] _as = new String[0];
    public long[] _al = new long[0];
    public Object[] _am = new Object[0];
}
