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

    int _total;
    int _hits;
    double _avg;
    double _dps;

    JLabel _totalLabel;
    JLabel _hitsLabel;
    JLabel _avgLabel;
    JLabel _dpsLabel;

    JPanel _overall;

    //TODO, make number a separate jpanel so only that needs to be updated
    public Entry(ItemManager im , AttackTuple at, int initialHit){

        _weaponId = at._weaponId;
        _enemyId = at._enemyId;
        _attackType = at._attackType;
        _im = im;

        _total = initialHit;
        _hits = 1;
        _avg = initialHit;
        _dps = 11.11;


        JPanel ret = new JPanel();

        ret.setLayout(new GridLayout(2,1)); //top and bottom

        JPanel upper = new JPanel();
        JLabel itemLabel = new JLabel();
        AsyncBufferedImage itemImage = im .getImage(_weaponId, 1, false);
        itemImage.addTo(itemLabel);
        upper.add(itemLabel);
        upper.add(new JLabel(at._enemyId));
        upper.add(new JLabel(at._attackType));


        JPanel lower = new JPanel();
        _totalLabel = new JLabel("Total: " + _total);
        _hitsLabel = new JLabel("Hits: " + _hits);;
        _avgLabel = new JLabel("Avg: " + _avg);;
        _dpsLabel = new JLabel("DPS: " + _dps);;


        lower.add(_totalLabel);
        lower.add(_hitsLabel);
        lower.add(_avgLabel);
        lower.add(_dpsLabel);
        lower.setLayout(new GridLayout(4,1));

        ret.add(upper);
        ret.add(lower);

        _overall = ret;

    }


    public void addHit(int amt){
        _total += amt;
        _hits += 1;
        _avg = _total/(double)_hits;

        _totalLabel.setText("Total: " + _total);
        _hitsLabel.setText("Hits: " + _hits);
        _avgLabel.setText("Avg: " + _avg);
    }

    public JPanel getJPanel(){
        return _overall;
    }

}
