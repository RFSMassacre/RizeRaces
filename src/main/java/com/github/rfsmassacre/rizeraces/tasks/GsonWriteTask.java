package com.github.rfsmassacre.rizeraces.tasks;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;

public class GsonWriteTask implements Runnable
{
    private final OriginGson gson;

    public GsonWriteTask()
    {
        this.gson = RizeRaces.getInstance().getOriginGson();
    }

    @Override
    public void run()
    {
        gson.writeAllAsync();
    }
}
