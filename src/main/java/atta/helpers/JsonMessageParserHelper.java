package atta.helpers;

import atta.server.comand.model.CommandModel;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public final class JsonMessageParserHelper
{
    private static final String
            INTEGER_VALUES = ("_ai"),
            DOUBLES_VALUES = ("_ad"),
            STRING_VALUES = ("_as"),
            LONG_VALUES = ("_al"),
            COMMAND = ("_cmd"),
            MODELS_VALUES = ("_md");

    public static String parse(CommandModel model)
    {
        try
        {
            return parseSimple(model);
        } catch (JSONException e)
        {
            return "";
        }
    }

    public static CommandModel parse(String json) throws Exception
    {
        return json.length() > 1000 ? parseIterator(json) : parseSimple(json);
    }

    private static String parseSimple(CommandModel model) throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put(COMMAND, model._cmd);
        json.put(INTEGER_VALUES, new JSONArray(model._ai));
        json.put(DOUBLES_VALUES, new JSONArray(model._ad));
        json.put(STRING_VALUES, new JSONArray(model._as));
        json.put(LONG_VALUES, new JSONArray(model._al));
        json.put(MODELS_VALUES, new JSONArray(model._am));
        return json.toString();
    }

    private static CommandModel parseSimple(String json) throws JSONException
    {
        CommandModel m = new CommandModel();

        JSONObject obj = null;
        try
        {
            obj = new JSONObject(json);
        } catch (Exception e)
        {
            System.err.println("Uncorrect message" + json);
            return null;
        }
        m._cmd = obj.getInt(COMMAND);
        try
        {
            m._ai = getIntegers(obj.getJSONArray(INTEGER_VALUES));
        } catch (Exception e)
        {
        }
        try
        {
            m._ad = getDoubles(obj.getJSONArray(DOUBLES_VALUES));
        } catch (Exception e)
        {
        }
        try
        {
            m._as = getStrings(obj.getJSONArray(STRING_VALUES));
        } catch (Exception e)
        {
        }
        try
        {
            m._al = getLongs(obj.getJSONArray(LONG_VALUES));
        } catch (Exception e)
        {
        }
        return m;
    }

    private static CommandModel parseIterator(String json) throws IOException
    {
        CommandModel m = new CommandModel();
        Any any = JsonIterator.parse(json).readAny();
        m._cmd = any.get(COMMAND).toInt();
        m._ai = getIntegers(any.get(INTEGER_VALUES));
        m._ad = getDoubles(any.get(DOUBLES_VALUES));
        m._as = getStrings(any.get(STRING_VALUES));
        m._al = getLongs(any.get(LONG_VALUES));
        return m;
    }

    private static long[] getLongs(JSONArray arr) throws JSONException
    {
        int length = arr.length();
        long[] longValues = new long[arr.length()];
        for (int i = 0; i < length; i++)
        {
            longValues[i] = arr.getLong(i);
        }
        return longValues;
    }

    private static long[] getLongs(Any array)
    {
        int length = array.size();
        long[] arr = new long[length];
        for (int i = 0; i < length; i++)
        {
            arr[i] = array.get(i).toLong();
        }
        return arr;
    }

    private static String[] getStrings(JSONArray arr) throws JSONException
    {
        int length = arr.length();
        String[] stringValues = new String[length];
        for (int i = 0; i < length; i++)
        {
            stringValues[i] = arr.getString(i);
        }
        return stringValues;
    }

    private static String[] getStrings(Any array)
    {
        int length = array.size();
        String[] arr = new String[length];
        for (int i = 0; i < length; i++)
        {
            arr[i] = array.get(i).toString();
        }
        return arr;
    }

    private static double[] getDoubles(JSONArray arr) throws JSONException
    {
        int length = arr.length();
        double[] doubleValues = new double[length];
        for (int i = 0; i < length; i++)
        {
            doubleValues[i] = arr.getDouble(i);
        }
        return doubleValues;
    }

    private static double[] getDoubles(Any array)
    {
        int length = array.size();
        double[] arr = new double[length];
        for (int i = 0; i < length; i++)
        {
            arr[i] = array.get(i).toDouble();
        }
        return arr;
    }

    private static int[] getIntegers(JSONArray arr) throws JSONException
    {
        int length = arr.length();
        int[] intValues = new int[length];
        for (int i = 0; i < length; i++)
        {
            intValues[i] = arr.getInt(i);
        }
        return intValues;
    }

    private static int[] getIntegers(Any array)
    {
        int length = array.size();
        int[] arr = new int[length];
        for (int i = 0; i < length; i++)
        {
            arr[i] = array.get(i).toInt();
        }
        return arr;
    }
}
