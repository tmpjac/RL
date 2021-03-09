package net.runelite.client.plugins.dps;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.AsyncBufferedImage;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;




public class DPSPanel extends PluginPanel {

    public class Tuple<X, Y> {
        public  X hits;
        public  Y damage;
        public Tuple(X hits, Y damage) {
            this.hits = hits;
            this.damage = damage;
        }
    }

    //HashMap<AttackTuple,Entry> _entries;
    ArrayList<Entry> _order;
    HashMap<AttackTuple, Tuple<Integer,Integer>> _stats;

    ItemManager _im;

    public static JPanel createEntry(ItemManager im){

        JPanel ret = new JPanel();

        ret.setLayout(new GridLayout(2,1)); //top and bottom

        JPanel upper = new JPanel();
        JLabel itemLabel = new JLabel();
        AsyncBufferedImage itemImage = im .getImage(1275, 1, false);
        itemImage.addTo(itemLabel);
        upper.add(itemLabel);


        JPanel lower = new JPanel();
        lower.add(new JLabel("Total: " + 25));
        lower.add(new JLabel("Hits: " + 5));
        lower.add(new JLabel("Average: " + 5));
        lower.add(new JLabel("DPS: " + 2));
        lower.setLayout(new GridLayout(4,1));




        ret.add(upper);
        ret.add(lower);

        return ret;
    }



    public DPSPanel(ItemManager im) {
        setBackground(Color.GREEN);
        //setLayout(new GridLayout(1,2)); //left and right
        _order = new ArrayList<Entry>();
        _stats = new HashMap<AttackTuple,Tuple<Integer,Integer>>();
        _im = im;

    }

    /*public void recalculatePanels(){
        for (AttackTuple at: _entries.keySet()){
            remove(_entries.get(at).getJPanel());
        }
        for (AttackTuple at: _entries.keySet()){
            add(_entries.get(at).getJPanel());
        }
    }*/


    public Entry findRelevant(AttackTuple at,DPSConfig config){
        //loop through order
        for (Entry e :  _order){
            if (
                    e._weaponId == at._weaponId &&
                    e._enemyId == at._enemyId &&
                    e._attackType == at._attackType //will be empty string based on config because passing in restricted AT
            ){
                System.out.println("Found " + at);
                return e;
            }
        }
        System.out.println("DID NOT FIND: " + at);
        return null;
    }


    //find releveant JPanel based on current config
    //update values
    //move to top
    public void update(AttackTuple at, int amt, DPSConfig config){
        AttackTuple restricted = new AttackTuple(
                at._weaponId,at._enemyId,
                config.separateAttackStyle()?at._attackType:""
        );
        Entry e = findRelevant(restricted,config);
        if (null==e){
            e = new Entry(_im, restricted, amt);
            _order.add(e);
            JPanel temp = e.getJPanel();
            add(temp,0);
        }else{
            e.addHit(amt);
            JPanel temp = e.getJPanel();
            remove(temp);
            add(temp,0);
        }
        revalidate();
        repaint();
    }

    public void addHit(AttackTuple at, int amt){

        if(!_stats.containsKey(at)) {
            //_order.add(at);
            Tuple<Integer, Integer> t = new Tuple<>(1,amt);
            _stats.put(at,t);
        }else {
            //_order.remove(at);
            //_order.add(at);//move to back
            _stats.get(at).hits = _stats.get(at).hits + 1;
            _stats.get(at).damage = _stats.get(at).damage + 1;
        }


    }


}
