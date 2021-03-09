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
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.VarbitChanged;
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


import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
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

    private HashMap<String,AttackTuple> _lastGearOnTarget;
    private String _lastEnemy;

    enum AttackStyle {
        ACCURATE("Accurate"),
        AGGRESSIVE("Aggressive"),
        DEFENSIVE("Defensive"),
        CONTROLLED("Controlled"),
        RANGING("Ranging"),
        LONGRANGE("Longrange"),
        CASTING("Casting"),
        DEFENSIVE_CASTING("Defensive Casting"),
        OTHER("Other");

        String _description;

        AttackStyle(String description){
            _description = description;
        }
    }


    AttackStyle[][] _weaponTypes = {
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE},
            {AttackStyle.RANGING, AttackStyle.RANGING, null, AttackStyle.LONGRANGE},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.CONTROLLED, AttackStyle.DEFENSIVE},
            {AttackStyle.RANGING, AttackStyle.RANGING, null, AttackStyle.LONGRANGE},
            {AttackStyle.AGGRESSIVE, AttackStyle.RANGING, AttackStyle.CASTING, null},
            {AttackStyle.RANGING, AttackStyle.RANGING, null, AttackStyle.LONGRANGE},
            {AttackStyle.OTHER, AttackStyle.AGGRESSIVE, null, null},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.CONTROLLED, AttackStyle.DEFENSIVE},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE},
            {AttackStyle.CONTROLLED, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE},
            {AttackStyle.CONTROLLED, AttackStyle.CONTROLLED, AttackStyle.CONTROLLED, AttackStyle.DEFENSIVE},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.CONTROLLED, AttackStyle.DEFENSIVE},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE, AttackStyle.CASTING, AttackStyle.DEFENSIVE_CASTING},
            {AttackStyle.RANGING, AttackStyle.RANGING, null, AttackStyle.LONGRANGE},
            {AttackStyle.ACCURATE, AttackStyle.CONTROLLED, null, AttackStyle.DEFENSIVE},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE, AttackStyle.CASTING, AttackStyle.DEFENSIVE_CASTING},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, AttackStyle.DEFENSIVE},
            {AttackStyle.CASTING, AttackStyle.CASTING, null, AttackStyle.DEFENSIVE_CASTING},
            {AttackStyle.ACCURATE, AttackStyle.AGGRESSIVE, AttackStyle.CONTROLLED, AttackStyle.DEFENSIVE},
            {AttackStyle.CONTROLLED, AttackStyle.AGGRESSIVE, null, AttackStyle.DEFENSIVE},
            {AttackStyle.AGGRESSIVE, AttackStyle.AGGRESSIVE, null, AttackStyle.AGGRESSIVE},
            {AttackStyle.ACCURATE, null, null, AttackStyle.OTHER}
    };

    @Override
    protected void startUp() throws Exception
    {
        _count = 0;
        //dpsPanel = injector.getInstance(DPSPanel2.class);
        dpsPanel = new DPSPanel(itemManager);
        _lastGearOnTarget = new HashMap<String,AttackTuple>();
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
        //recreate panels and redraw
        //dps panel has a map<AttackTuple,Entry)
        //dpsPanel.recalculatePanels();

    }



	@Subscribe
	public void onStatChanged(StatChanged s){
		if (s.getSkill() ==  Skill.HITPOINTS ) {
		    //System.out.println("HP stat changed");
            //assign weapon and armor
            AttackTuple at = _lastGearOnTarget.get(_lastEnemy);

            int currentAttackStyleVarbit = client.getVar(VarPlayer.ATTACK_STYLE);
            int currentEquippedWeaponTypeVarbit = client.getVar(Varbits.EQUIPPED_WEAPON_TYPE);
            int currentCastingModeVarbit = client.getVar(Varbits.DEFENSIVE_CASTING_MODE);

            Item weapon = client.getItemContainer(InventoryID.EQUIPMENT).getItems()[EquipmentInventorySlot.WEAPON.getSlotIdx()];
            AttackTuple atNew =new AttackTuple(weapon.getId(),_lastEnemy, _weaponTypes[currentEquippedWeaponTypeVarbit][currentAttackStyleVarbit]._description);

            System.out.println("HP Drop    " + atNew);

            _lastGearOnTarget.put(
                    _lastEnemy,
                    atNew
            );
        }
	}


 /*Not needed because on hitsplat will check for current attack style
    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        int currentAttackStyleVarbit = client.getVar(VarPlayer.ATTACK_STYLE);
        int currentEquippedWeaponTypeVarbit = client.getVar(Varbits.EQUIPPED_WEAPON_TYPE);
        int currentCastingModeVarbit = client.getVar(Varbits.DEFENSIVE_CASTING_MODE);

        if ( "" != _lastEnemy && null != _lastGearOnTarget) {

            System.out.println("combat type: " + _weaponTypes[currentEquippedWeaponTypeVarbit][currentAttackStyleVarbit]._description);
            AttackTuple at = _lastGearOnTarget.get(_lastEnemy);

            _lastGearOnTarget.put(
                    _lastEnemy,
                    new AttackTuple(at._weaponId, at._enemyId, _weaponTypes[currentEquippedWeaponTypeVarbit][currentAttackStyleVarbit]._description)
            );
        }


    }*/



    @Subscribe
    public void onInteractingChanged(InteractingChanged interactingChanged){
        Actor target = interactingChanged.getTarget();
        Actor source = interactingChanged.getSource();
        if (null == target || !(target instanceof  NPC) || !(source == client.getLocalPlayer())) return;
        System.out.println("Interacting " + target.getName() + target.getCombatLevel());
        _lastEnemy = target.getName() + target.getCombatLevel(); //barbarian17
    }


    @Subscribe
    public void onHitsplatApplied(HitsplatApplied h){

        if(h.getHitsplat().isMine() ){

            //System.out.println("Hitsplat applied " + h.getActor());
            //System.out.println(h.getActor());
            //System.out.println(client.getLocalPlayer());
            if(h.getActor() != client.getLocalPlayer()) {
                AttackTuple at = _lastGearOnTarget.get(h.getActor().getName()+h.getActor().getCombatLevel());
                dpsPanel.addHit(at, h.getHitsplat().getAmount());
                SwingUtilities.invokeLater(() -> {
                            dpsPanel.update(at, h.getHitsplat().getAmount(), dpsConfig);
                });
                System.out.println("Hitsplat    " + at);


            }
        }
    }

}
