/*
 * Copyright (c) 2019 Hydrox6 <ikada@protonmail.ch>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.dps;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;


import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;

/*
Create a new repo at github.
Clone the repo from fedorahosted to your local machine.
git remote rename origin upstream
git remote add origin https://github.com/tmpjac/RL
git push origin master


Now you can work with it just like any other github repo. To pull in patches from upstream, simply run git pull upstream master && git push origin master.
 */



@PluginDescriptor(
        name = "DPS2",
        description = "Shows the current ammo the player has equipped2",
        tags = {"bolts", "darts", "chinchompa", "equipment"}
)
public class DPSPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private ItemManager itemManager;

    @Inject
    private DPSConfig dpsConfig;

    @Provides
    DPSConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DPSConfig.class);
    }

    private int _count;

    private NavigationButton navButton;
    private DPSPanel dpsPanel;
    private int _initialHP;

    private HashMap<String,AttackTuple> _lastGearAgainstEnemy;
    private String _lastEnemy;

    private HashMap<AttackTuple,DamageTuple> _ats;
    private ArrayList<AttackTuple> _atOrder;


    @Override
    protected void startUp() throws Exception
    {
        _count = 0;
        //dpsPanel = injector.getInstance(DPSPanel2.class);

        _ats = new HashMap<AttackTuple,DamageTuple>();
        _atOrder = new ArrayList<AttackTuple>();


        dpsPanel = new DPSPanel(itemManager,_ats, _atOrder, dpsConfig);
        _lastGearAgainstEnemy = new HashMap<String,AttackTuple>();
        _lastEnemy = "";




        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "mail.png");
        _initialHP = -1;


        navButton = NavigationButton.builder()
                .tooltip("DPS")
                .icon(icon)
                .priority(5)
                .panel(dpsPanel)
                .build();

        clientToolbar.addNavigation(navButton);


    }

    @Override
    protected void shutDown() throws Exception
    {

    }


    @Subscribe
    public  void onConfigChanged(ConfigChanged cc){
        System.out.println("Config changed");
        System.out.println(cc.getNewValue());
        SwingUtilities.invokeLater(() -> {
            dpsPanel.redrawAll();
        });


    }


    //set lastEnemy
    @Subscribe
    public void onInteractingChanged(InteractingChanged interactingChanged){
        Actor target = interactingChanged.getTarget();
        Actor source = interactingChanged.getSource();
        if (null == target || !(target instanceof  NPC) || !(source == client.getLocalPlayer())) return;
        //System.out.println("Interacting " + target.getName() + target.getCombatLevel());
        _lastEnemy = target.getName() + target.getCombatLevel(); //barbarian17
    }



    //capture AttackTuple and save with respect to enemy
    @Subscribe
    public void onAnimationChanged(AnimationChanged event){


        if (event.getActor() instanceof Player && event.getActor().getName() != null){
            Player eventSource = (Player)event.getActor();

            if (eventSource == null || eventSource.getName() == null || eventSource.getInteracting() == null || eventSource.getInteracting().getName() == null) return;


            if (eventSource.equals(client.getLocalPlayer())){

                AnimationData ad = AnimationData.dataForAnimation(eventSource.getAnimation());
                if (ad == null) return;

                System.out.println(ad.attackStyle);
                System.out.println(ad.animationId);

                int currentAttackStyleVarbit = client.getVar(VarPlayer.ATTACK_STYLE);
                int currentEquippedWeaponTypeVarbit = client.getVar(Varbits.EQUIPPED_WEAPON_TYPE);
                int currentCastingModeVarbit = client.getVar(Varbits.DEFENSIVE_CASTING_MODE);


                Item weapon = client.getItemContainer(InventoryID.EQUIPMENT).getItems()[EquipmentInventorySlot.WEAPON.getSlotIdx()];
                ItemStats stats = itemManager.getItemStats(weapon.getId(),false);
                System.out.println(stats);

                int attackSpeed = stats.getEquipment().getAspeed();

                AttackStyle[] attackStyles = WeaponType.getWeaponType(currentEquippedWeaponTypeVarbit).getAttackStyles();

                AttackStyle attackStyle = attackStyles[currentAttackStyleVarbit];
                if (attackStyle == AttackStyle.CASTING && currentCastingModeVarbit == 1){
                    attackStyle = AttackStyle.DEFENSIVE_CASTING;
                }

                if (attackStyle == AttackStyle.RAPID){
                    attackSpeed -= 1;
                }


                Prayer[] combatPrayers = {Prayer.HAWK_EYE, Prayer.MYSTIC_LORE, Prayer.MYSTIC_MIGHT, Prayer.EAGLE_EYE, Prayer.CHIVALRY, Prayer.PIETY, Prayer.RIGOUR, Prayer.AUGURY};
                Prayer activePrayer = null;
                String activePrayerName = "";
                for (Prayer p : combatPrayers){
                    if(client.isPrayerActive(p) && ad.attackStyle.isUsingSuccessfulOffensivePray(p)){
                        activePrayer = p;
                        activePrayerName = p.name();
                    }
                }


                System.out.println("Attack Speed: " + attackSpeed);
                System.out.println("Style: " +  attackStyle);
                System.out.println("Prayer: " + activePrayer);

                AttackTuple atNew =new AttackTuple(weapon.getId(),_lastEnemy,attackSpeed,activePrayerName);



                //System.out.println("HP Drop    " + atNew);

                _lastGearAgainstEnemy.put(
                        _lastEnemy,
                        atNew
                );


            }


        }





    }


    //lookup AttackTuple on enemy, add AttackTuple to _ats, and update DPSPanel
    @Subscribe
    public void onHitsplatApplied(HitsplatApplied h){

        if(h.getHitsplat().isMine() ){

            //System.out.println("Hitsplat applied " + h.getActor());
            //System.out.println(h.getActor());
            //System.out.println(client.getLocalPlayer());
            if(h.getActor() != client.getLocalPlayer()) {
                AttackTuple at = _lastGearAgainstEnemy.get(h.getActor().getName()+h.getActor().getCombatLevel());
                if (null == at) return; //if first hit is a splash, no gear info exists. for now ignore
                if (_ats.containsKey(at)){
                    DamageTuple curr = _ats.get(at);
                    curr._hits += 1;
                    curr._damage += h.getHitsplat().getAmount();
                    if (!at._prayer.equals("")) curr._numOnPrayer += 1;
                    _atOrder.remove(at);
                    _atOrder.add(at); //add to the end of order
                }else{
                    _ats.put(at,new DamageTuple(h.getHitsplat().getAmount(),at._prayer.equals("")?0:1));
                    _atOrder.add(at); //add to the end of order
                }

                SwingUtilities.invokeLater(() -> {
                    dpsPanel.update(at, h.getHitsplat().getAmount());
                });
                //System.out.println("Hitsplat    " + at);


            }
        }
    }

}
