package com.github.rfsmassacre.rizeraces.tasks;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.data.PartyGson;

public class GsonWriteTask implements Runnable
{
    private final OriginGson originGson;
    private final PartyGson partyGson;

    public GsonWriteTask()
    {
        this.originGson = RizeRaces.getInstance().getOriginGson();
        this.partyGson = RizeRaces.getInstance().getPartyGson();
    }

    @Override
    public void run()
    {
        originGson.writeAllAsync();
        partyGson.writeAllAsync();
    }
}
