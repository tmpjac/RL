package net.runelite.client.plugins.dps;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import static net.runelite.client.plugins.dps.AttackStyle.ACCURATE;
import static net.runelite.client.plugins.dps.AttackStyle.ACCURATE_CASTING;
import static net.runelite.client.plugins.dps.AttackStyle.ACCURATE_RANGING;
import static net.runelite.client.plugins.dps.AttackStyle.AGGRESSIVE;
import static net.runelite.client.plugins.dps.AttackStyle.AIM_AND_FIRE;
import static net.runelite.client.plugins.dps.AttackStyle.BLOCK;
import static net.runelite.client.plugins.dps.AttackStyle.CASTING;
import static net.runelite.client.plugins.dps.AttackStyle.CONTROLLED;
import static net.runelite.client.plugins.dps.AttackStyle.DEFENSIVE;
import static net.runelite.client.plugins.dps.AttackStyle.DEFENSIVE_CASTING;
import static net.runelite.client.plugins.dps.AttackStyle.LONGRANGE;
import static net.runelite.client.plugins.dps.AttackStyle.LONGRANGE_CASTING;
import static net.runelite.client.plugins.dps.AttackStyle.RAPID;

enum WeaponType
{
    UNARMED(ACCURATE, AGGRESSIVE, null, DEFENSIVE),
    AXE(ACCURATE, AGGRESSIVE, AGGRESSIVE, DEFENSIVE),
    BLUNT(ACCURATE, AGGRESSIVE, null, DEFENSIVE),
    BOW(ACCURATE_RANGING, RAPID, null, LONGRANGE),
    CLAWS(ACCURATE, AGGRESSIVE, CONTROLLED, DEFENSIVE),
    CROSSBOW(ACCURATE_RANGING, RAPID, null, LONGRANGE),
    SALAMANDER(AGGRESSIVE, RAPID, CASTING, null),
    CHINCHOMPA(ACCURATE_RANGING, RAPID, null, LONGRANGE),
    GUN(AIM_AND_FIRE, AGGRESSIVE, null, null),
    SWORD_SLASH(ACCURATE, AGGRESSIVE, CONTROLLED, DEFENSIVE),
    SWORD_2H(ACCURATE, AGGRESSIVE, AGGRESSIVE, DEFENSIVE),
    PICKAXE(ACCURATE, AGGRESSIVE, AGGRESSIVE, DEFENSIVE),
    HALBERD(CONTROLLED, AGGRESSIVE, null, DEFENSIVE),
    POLESTAFF(ACCURATE, AGGRESSIVE, null, DEFENSIVE),
    SCYTHE(ACCURATE, AGGRESSIVE, AGGRESSIVE, DEFENSIVE),
    SPEAR(CONTROLLED, CONTROLLED, CONTROLLED, DEFENSIVE),
    SPIKED(ACCURATE, AGGRESSIVE, CONTROLLED, DEFENSIVE),
    SWORD_STAB(ACCURATE, AGGRESSIVE, AGGRESSIVE, DEFENSIVE),
    STAFF(ACCURATE, AGGRESSIVE, null, DEFENSIVE, CASTING, DEFENSIVE_CASTING),
    THROWN(ACCURATE_RANGING, RAPID, null, LONGRANGE),
    WHIP(ACCURATE, CONTROLLED, null, DEFENSIVE),
    STAFF_BLADED(ACCURATE, AGGRESSIVE, null, DEFENSIVE, CASTING, DEFENSIVE_CASTING),
    GODSWORD(ACCURATE, AGGRESSIVE, AGGRESSIVE, DEFENSIVE),
    STAFF_POWERED(ACCURATE_CASTING, ACCURATE_CASTING, null, LONGRANGE_CASTING),
    BANNER(ACCURATE, AGGRESSIVE, CONTROLLED, DEFENSIVE),
    POLEARM(CONTROLLED, AGGRESSIVE, null, DEFENSIVE),
    BLUDGEON(AGGRESSIVE, AGGRESSIVE, null, AGGRESSIVE),
    BULWARK(ACCURATE, null, null, BLOCK);

    private final AttackStyle[] attackStyles;

    private static final Map<Integer, WeaponType> weaponTypes;

    static
    {
        ImmutableMap.Builder<Integer, WeaponType> builder = new ImmutableMap.Builder<>();

        for (WeaponType weaponType : values())
        {
            builder.put(weaponType.ordinal(), weaponType);
        }

        weaponTypes = builder.build();
    }

    WeaponType(AttackStyle... attackStyles)
    {
        this.attackStyles = attackStyles;
    }

    public AttackStyle[] getAttackStyles()
    {
        return attackStyles;
    }

    public static WeaponType getWeaponType(int id)
    {
        return weaponTypes.get(id);
    }
}