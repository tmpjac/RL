package net.runelite.client.plugins.dps;

import net.runelite.client.util.AsyncBufferedImage;

import javax.swing.*;
import java.awt.*;
import net.runelite.client.game.ItemManager;

public class Entry {


    ItemManager _im;
    int _weaponId;
    String _enemyId;
    String _attackType;
    String _prayer;

    int _total;
    int _hits;
    double _avg;
    double _ticks;
    int _onPrayer;

    JLabel _totalLabel;
    JLabel _hitsLabel;
    JLabel _avgLabel;
    JLabel _dpsLabel;
    JLabel _ticksLabel;
    JLabel _prayerLabel;

    JPanel _overall;

    //TODO, make number a separate jpanel so only that needs to be updated
    public Entry(ItemManager im , AttackTuple at, int total, int hits, String prayer){

        _weaponId = at._weaponId;
        _enemyId = at._enemyId;
        _prayer = at._prayer;
        _prayer = prayer;
        _im = im;


        _total = total;
        _hits = hits;
        _avg = total/(double)hits;
        _ticks = _hits*at._attackSpeed;

        double dps = _total / (double) _ticks;


        JPanel ret = new JPanel();

        ret.setLayout(new GridLayout(2,1)); //top and bottom

        JPanel upper = new JPanel();
        JLabel itemLabel = new JLabel();
        AsyncBufferedImage itemImage = im .getImage(_weaponId, 1, false);
        itemImage.addTo(itemLabel);
        upper.add(itemLabel);
        upper.add(new JLabel(at._enemyId));
        upper.add(new JLabel(at._prayer));

        JPanel lower = new JPanel();
        _hitsLabel = new JLabel("Hits: " + _hits);;
        _totalLabel = new JLabel("Damage: " + _total);
        _avgLabel = new JLabel("Avg: " + _avg);;
        _ticksLabel = new JLabel("Ticks: " + _ticks);;
        _dpsLabel = new JLabel("DPS: " + dps);;
        //_prayerLabel = new JLabel("On Prayer: " + _onPrayer);


        lower.add(_hitsLabel);
        lower.add(_totalLabel);
        lower.add(_avgLabel);
        lower.add(_ticksLabel);
        lower.add(_dpsLabel);
        //lower.add(_prayerLabel);
        lower.setLayout(new GridLayout(4,1));

        ret.add(upper);
        ret.add(lower);

        _overall = ret;

    }


    public void addHit(int amt, int attackSpeed, int numOnPrayer){
        _total += amt;
        _hits += 1;
        _avg = _total/(double)_hits;
        _ticks += attackSpeed;
        //_onPrayer += numOnPrayer;

        double dps = _total / (double) _ticks;

        _hitsLabel.setText("Hits: " + _hits);
        _totalLabel.setText("Damage: " + _total);
        _avgLabel.setText("Avg: " + _avg);
        _ticksLabel.setText("Ticks: " + _ticks);
        _dpsLabel.setText("DPS: " + dps);
        //_prayerLabel.setText("On Prayer: " + _onPrayer);
    }

    public void addHits(int amt, int hits, int attackSpeed, int numOnPrayer){
        _total += amt;
        _hits += hits;
        _avg = _total/(double)_hits;
        _ticks = _ticks + (hits*attackSpeed);
        //_onPrayer += numOnPrayer;

        double dps = _total / (double) _ticks;

        _hitsLabel.setText("Hits: " + _hits);
        _totalLabel.setText("Damage: " + _total);
        _avgLabel.setText("Avg: " + _avg);
        _ticksLabel.setText("Ticks: " + _ticks);
        _dpsLabel.setText("DPS: " + dps);
        //_prayerLabel.setText("On Prayer: " + _onPrayer);
    }


    public JPanel getJPanel(){
        return _overall;
    }

}
