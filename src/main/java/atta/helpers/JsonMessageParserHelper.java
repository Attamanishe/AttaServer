package atta.helpers;

import atta.server.comand.model.CommandModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonMessageParserHelper
{
    private static final String
            INTEGER_VALUES = ("_ai"),
            DOUBLES_VALUES = ("_ad"),
            STRING_VALUES = ("_as"),
            LONG_VALUES = ("_al"),
            COMMAND = ("_cmd"),
            MODELS_VALUES = ("_am");

    public static String parse(CommandModel model)
    {
        return parseSimple(model);
    }

    public static CommandModel parse(String json) throws Exception
    {
        return parseSimple(json);
    }

    private static String parseSimple(CommandModel model) throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put(COMMAND, model._cmd);

        if (model._ai != null & model._ai.length > 0)
        {
            json.put(INTEGER_VALUES, new JSONArray(model._ai));
        }
        if (model._ad != null & model._ad.length > 0)
        {
            json.put(DOUBLES_VALUES, new JSONArray(model._ad));
        }
        if (model._as != null & model._as.length > 0)
        {
            json.put(STRING_VALUES, new JSONArray(model._as));
        }
        if (model._al != null & model._al.length > 0)
        {
            json.put(LONG_VALUES, new JSONArray(model._al));
        }
        if (model._am != null & model._am.length > 0)
        {
            json.put(MODELS_VALUES, new JSONArray(model._am));
        }

        return json.toString();
    }


    private static CommandModel parseSimple(String json) throws JSONException
    {
        CommandModel m = new CommandModel();

        JSONObject obj = null;

        obj = new JSONObject(json);
        if (obj == null)
        {
            System.err.println("Uncorrect message" + json);
            return null;
        }

        m._cmd = obj.getInt(COMMAND);
        if (obj.has(INTEGER_VALUES))
        {
            m._ai = getIntegers(obj.getJSONArray(INTEGER_VALUES));
        }

        if (obj.has(DOUBLES_VALUES))
        {
            m._ad = getDoubles(obj.getJSONArray(DOUBLES_VALUES));
        }

        if (obj.has(STRING_VALUES))
        {
            m._as = getStrings(obj.getJSONArray(STRING_VALUES));
        }

        if (obj.has(LONG_VALUES))
        {
            m._al = getLongs(obj.getJSONArray(LONG_VALUES));
        }

        if (obj.has(MODELS_VALUES))
        {
            m._am = getModels(obj.getJSONArray(MODELS_VALUES));
        }
        return m;
    }

    private static long[] getLongs(JSONArray arr) throws JSONException
    {
        int length = arr.length();
        long[] longValues = new long[arr.length()];
        for (int i = 0; i < length; i++)
        {
            longValues[i] = arr.optLong(i);
        }
        return longValues;
    }

    private static String[] getStrings(JSONArray arr) throws JSONException
    {
        int length = arr.length();
        String[] stringValues = new String[length];
        for (int i = 0; i < length; i++)
        {
            stringValues[i] = arr.optString(i);
        }
        return stringValues;
    }

    private static double[] getDoubles(JSONArray arr) throws JSONException
    {
        int length = arr.length();
        double[] doubleValues = new double[length];
        for (int i = 0; i < length; i++)
        {
            doubleValues[i] = arr.optDouble(i);
        }
        return doubleValues;
    }

    private static int[] getIntegers(JSONArray arr) throws JSONException
    {
        int length = arr.length();
        int[] intValues = new int[length];
        for (int i = 0; i < length; i++)
        {
            intValues[i] = arr.optInt(i);
        }
        return intValues;
    }

    private static Object[] getModels(JSONArray arr) throws JSONException
    {
        int length = arr.length();
        Object[] serializables = new Object[length];
        for (int i = 0; i < length; i++)
        {
            serializables[i] = arr.opt(i);
        }
        return serializables;
    }
}