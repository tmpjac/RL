package net.runelite.client.plugins.dps;

public class DamageTuple {


    public int _hits;
    public int _damage;
    public int _numOnPrayer;

    public DamageTuple(int initialAmount, int onPrayer){
        _hits = 1;
        _damage = initialAmount;
        _numOnPrayer = onPrayer;
    }



}
