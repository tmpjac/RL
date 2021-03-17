package net.runelite.client.plugins.dps;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.AsyncBufferedImage;

import java.lang.reflect.Array;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;




public class DPSPanel extends PluginPanel {


    //HashMap<AttackTuple,Entry> _entries;
    ArrayList<Entry> _entries;

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

    public HashMap<AttackTuple,DamageTuple> _ats;
    public ArrayList<AttackTuple> _atOrder;

    public ArrayList<AttackTuple> _ignores;
    public Set<AttackTuple> _favorites;
    public boolean _showAll;
    public boolean _showIgnores;
    public DPSConfig _config;


    public DPSPanel(ItemManager im, HashMap<AttackTuple,DamageTuple> ats,ArrayList<AttackTuple> atOrder, DPSConfig config) {
        _ats = ats;
        setBackground(Color.GREEN);
        //setLayout(new GridLayout(1,2)); //left and right
        _entries = new ArrayList<Entry>();
        _atOrder = atOrder;
        _im = im;
        _ignores = new ArrayList<AttackTuple>();
        _favorites = new HashSet<AttackTuple>();
        _showIgnores = false;
        _showAll = true;
        _config = config;

        JButton showAll = new JButton("All");
        showAll.addActionListener(ev -> {
            _showAll = true;
            SwingUtilities.invokeLater(() -> {
                redrawAll();
            });
        });
        JButton showFavorites = new JButton("Favorites");
        showFavorites.addActionListener(ev -> {
            _showAll = false;
            SwingUtilities.invokeLater(() -> {
                redrawAll();
            });
        });

        add(showAll);
        add(showFavorites);

    }


    public boolean compareWithConfig(AttackTuple at1, AttackTuple at2){
        return
                at1._weaponId == at2._weaponId &&
                at1._enemyId.equals( at2._enemyId) &&
                //(at1._attackType.equals(at2._attackType) || !_config.separateAttackStyle()) && //either matches or unchecked
                (at1._prayer.equals(at2._prayer) || !_config.separatePrayer()); //either matches or unchecked
    }

    public boolean anyMatchWithConfig(ArrayList<AttackTuple> ats, AttackTuple at1 ){
        for (AttackTuple at2 : ats){
            if (compareWithConfig(at1,at2)){
                return true;
            }
        }
        return false;
    }

    public boolean anyMatch(ArrayList<AttackTuple> ats, AttackTuple at1 ){
        for (AttackTuple at2 : ats){
            if (at1.equals(at2)){
                return true;
            }
        }
        return false;
    }


    public Entry findRelevant(AttackTuple at,DPSConfig config){
        //loop through order
        for (Entry e :  _entries){
            //System.out.println("     Considering "  + e._weaponId + "," + e._enemyId + "," + e._attackType );
            //System.out.println("     " +  (e._weaponId == at._weaponId) + "," + (e._enemyId == at._enemyId)+ "," + (e._attackType == at._attackType));
            if (
                    e._weaponId == at._weaponId &&
                    e._enemyId.equals( at._enemyId) &&
                    //(e._attackType.equals(at._attackType) || !config.separateAttackStyle()) &&//will be empty string based on config because passing in restricted AT
                    (e._prayer.equals(at._prayer) || !config.separatePrayer()) //will be empty string based on config because passing in restricted AT
            ){
                //System.out.println("Found " + at);
                return e;
            }
        }
        //System.out.println("DID NOT FIND: " + at );
        return null;
    }



    public void removeRelevant(AttackTuple restricted,DPSConfig config){
        //remove from DPSPlugin._ats that was passed in constructor based on if match given Config

        _ats.keySet().removeIf(at ->
                restricted._weaponId == at._weaponId &&
                restricted._enemyId.equals( at._enemyId) &&
                //(restricted._attackType.equals(at._attackType) || !config.separateAttackStyle()) && //either matches or unchecked
                (restricted._prayer.equals(at._prayer) || !config.separatePrayer()) //either matches or unchecked
        );

        _atOrder.removeIf(at ->
                restricted._weaponId == at._weaponId &&
                        restricted._enemyId.equals( at._enemyId) &&
                       // (restricted._attackType.equals(at._attackType) || !config.separateAttackStyle()) && //either matches or unchecked
                        (restricted._prayer.equals(at._prayer) || !config.separatePrayer()) //either matches or unchecked
        );

    }




    public void addMenuToPanel(JPanel panel, AttackTuple restricted, DPSConfig config){
        //right-click menu
        JMenuItem reset = new JMenuItem("Reset");
        reset.addActionListener(ev -> {
            System.out.println("removing " + restricted._enemyId + " " + restricted._weaponId );
            _entries.remove(panel); //this remove panel info from DPSPanel but data still exists in DPSPlugin._ats
            remove(panel);
            removeRelevant(restricted,config);
            revalidate();
            repaint();
        });

        JMenuItem favorite;
        if (_showAll) { //ALL TAB
            favorite = new JMenuItem("Favorite"); //shallow ignore. Keep adding to _ats but just choose not to display
            favorite.addActionListener(ev -> {
                System.out.println("favoriting " + restricted._enemyId + " " + restricted._weaponId);
                //markRelevantIgnored(restricted);
                _favorites.add(restricted); //set add monster to favorites
                if (_showAll) {
                    SwingUtilities.invokeLater(() -> {
                        redrawAll();
                    });
                }

            });
        }else { //FAVORITES TAB
            favorite = new JMenuItem("Un-favorite"); //shallow ignore. Keep adding to _ats but just choose not to display
            favorite.addActionListener(ev -> {
                System.out.println("un-favoriting " + restricted._enemyId + " " + restricted._weaponId );
                //markRelevantIgnored(restricted);
                _favorites.remove(restricted); //set add monster to favorites
                if (!_showAll) {
                    SwingUtilities.invokeLater(() -> {
                        redrawAll();
                    });
                }

            });
        }


        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5,5,5,5));
        popupMenu.add(reset);
        popupMenu.add(favorite);



        panel.setComponentPopupMenu(popupMenu);
    }




    public void redrawAll(){

        for (Entry e : _entries){
            remove(e.getJPanel());
        }
        _entries.clear();



        //to preserve order, I would need another array that keeps newest at the end instead of _ats.keySet()
        //for (AttackTuple at : _ats.keySet()) {
        for (AttackTuple at : _atOrder){
            //System.out.println("redrawAll : " + at);
            if (_showAll || _favorites.contains(at) ){
                AttackTuple restricted = new AttackTuple(
                        at._weaponId, at._enemyId,
                        at._attackSpeed,
                        _config.separatePrayer() ? at._prayer : ""
                );

                Entry e = findRelevant(restricted, _config);
                if (null == e) {
                    e = new Entry(_im, restricted, _ats.get(at)._damage, _ats.get(at)._hits, at._prayer);
                    _entries.add(e);
                    JPanel temp = e.getJPanel();
                    addMenuToPanel(temp, restricted, _config);
                    add(temp, 0); //add at the top which preserved order from ordered _ats
                } else {
                    e.addHits(_ats.get(at)._damage, _ats.get(at)._hits, at._attackSpeed, _ats.get(at)._numOnPrayer); //add to existing entry
                    JPanel temp = e.getJPanel();
                    remove(temp);
                    addMenuToPanel(temp, restricted, _config);
                    add(temp, 0);
                }

            }
            revalidate();
            repaint();
        }
    }

    //find relevant JPanel and update it
    public void update( AttackTuple at, int amt){

        if (_showAll || _favorites.contains(at._enemyId)){
            System.out.println("Updating");
            AttackTuple restricted = new AttackTuple(
                    at._weaponId,at._enemyId,
                    at._attackSpeed,
                    _config.separatePrayer()?at._prayer:""
            );

            Entry e = findRelevant(restricted,_config);
            if (null==e){
                e = new Entry(_im, restricted, amt,1, at._prayer);
                _entries.add(e);
                JPanel temp = e.getJPanel();
                addMenuToPanel(temp,restricted,_config);
                add(temp,0);
            }else{
                e.addHit(amt,at._attackSpeed, at._prayer.equals("")?0:1);
                JPanel temp = e.getJPanel();
                remove(temp);
                addMenuToPanel(temp,restricted,_config);
                add(temp,0);
            }
            revalidate();
            repaint();
        }else{
            System.out.println("not updating");
        }

    }

}
