package net.runelite.client.plugins.dps;

import com.google.common.collect.ImmutableMap;
import net.runelite.api.HeadIcon;
import net.runelite.api.Prayer;
import net.runelite.api.SpriteID;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.stream.Collectors;

public enum AnimationData
{
    // MELEE
    MELEE_DAGGER_SLASH(376, AttackStyle.SLASH), // tested w/ dds
    MELEE_SPEAR_STAB(381, AttackStyle.STAB), // tested w/ zammy hasta
    MELEE_SWORD_STAB(386, AttackStyle.STAB), // tested w/ dragon sword, obby sword, d long
    MELEE_SCIM_SLASH(390, AttackStyle.SLASH), // tested w/ rune & dragon scim, d sword, VLS, obby sword

    MELEE_GENERIC_SLASH(393, AttackStyle.SLASH), // tested w/ zuriel's staff, d long slash, dclaws regular slash
    MELEE_STAFF_CRUSH(0, AttackStyle.SLASH), // 393 previously, save name to support old fights but no longer track

    MELEE_BATTLEAXE_SLASH(395, AttackStyle.SLASH), // tested w/ rune baxe
    MELEE_MACE_STAB(400, AttackStyle.STAB), // tested w/ d mace
    MELEE_BATTLEAXE_CRUSH(401, AttackStyle.CRUSH), // tested w/ rune baxe, dwh & statius warhammer animation, d mace
    MELEE_2H_CRUSH(406, AttackStyle.CRUSH), // tested w/ rune & dragon 2h
    MELEE_2H_SLASH(407, AttackStyle.SLASH), // tested w/ rune & dragon 2h
    MELEE_STAFF_CRUSH_2(414, AttackStyle.CRUSH), // tested w/ ancient staff, 3rd age wand
    MELEE_STAFF_CRUSH_3(419, AttackStyle.CRUSH), // Common staff crush. Air/fire/etc staves, smoke battlestaff, SOTD/SOL crush, zammy hasta crush
    MELEE_PUNCH(422, AttackStyle.CRUSH),
    MELEE_KICK(423, AttackStyle.CRUSH),
    MELEE_STAFF_STAB(428, AttackStyle.STAB), // tested w/ SOTD/SOL jab, vesta's spear stab, c hally
    MELEE_SPEAR_CRUSH(429, AttackStyle.CRUSH), // tested w/ vesta's spear
    MELEE_STAFF_SLASH(440, AttackStyle.SLASH), // tested w/ SOTD/SOL slash, zammy hasta slash, vesta's spear slash, c hally
    MELEE_SCEPTRE_CRUSH(1058, AttackStyle.CRUSH), // tested w/ thammaron's sceptre, d long spec
    MELEE_DRAGON_MACE_SPEC(1060, AttackStyle.CRUSH, true),
    MELEE_DRAGON_DAGGER_SPEC(1062, AttackStyle.STAB, true),
    MELEE_DRAGON_WARHAMMER_SPEC(1378, AttackStyle.CRUSH, true), // tested w/ dwh, statius warhammer spec
    MELEE_ABYSSAL_WHIP(1658, AttackStyle.SLASH), // tested w/ whip, tent whip
    MELEE_GRANITE_MAUL(1665, AttackStyle.CRUSH), // tested w/ normal gmaul, ornate maul
    MELEE_GRANITE_MAUL_SPEC(1667, AttackStyle.CRUSH, true), // tested w/ normal gmaul, ornate maul
    MELEE_DHAROKS_GREATAXE_CRUSH(2066, AttackStyle.CRUSH),
    MELEE_DHAROKS_GREATAXE_SLASH(2067, AttackStyle.SLASH),
    MELEE_AHRIMS_STAFF_CRUSH(2078, AttackStyle.CRUSH),
    MELEE_OBBY_MAUL_CRUSH(2661, AttackStyle.CRUSH),
    MELEE_ABYSSAL_DAGGER_STAB(3297, AttackStyle.STAB),
    MELEE_ABYSSAL_BLUDGEON_CRUSH(3298, AttackStyle.CRUSH),
    MELEE_LEAF_BLADED_BATTLEAXE_CRUSH(3852, AttackStyle.CRUSH),
    MELEE_BARRELCHEST_ANCHOR_CRUSH(5865, AttackStyle.CRUSH),
    MELEE_LEAF_BLADED_BATTLEAXE_SLASH(7004, AttackStyle.SLASH),
    MELEE_GODSWORD_SLASH(7045, AttackStyle.SLASH), // tested w/ AGS, BGS, ZGS, SGS, AGS(or) sara sword
    MELEE_GODSWORD_CRUSH(7054, AttackStyle.CRUSH), // tested w/ AGS, BGS, ZGS, SGS, sara sword
    MELEE_DRAGON_CLAWS_SPEC(7514, AttackStyle.SLASH, true),
    MELEE_DRAGON_SWORD_SPEC(7515, AttackStyle.STAB, true), // also VLS spec
    MELEE_ELDER_MAUL(7516, AttackStyle.CRUSH),
    MELEE_ZAMORAK_GODSWORD_SPEC(7638, AttackStyle.SLASH, true), // tested zgs spec
    MELEE_ZAMORAK_GODSWORD_OR_SPEC(7639, AttackStyle.SLASH, true), // UNTESTED, assumed due to ags(or)
    MELEE_SARADOMIN_GODSWORD_SPEC(7640, AttackStyle.SLASH, true), // tested sgs spec
    MELEE_SARADOMIN_GODSWORD_OR_SPEC(7641, AttackStyle.SLASH, true), // UNTESTED, assumed due to ags(or)
    MELEE_BANDOS_GODSWORD_SPEC(7642, AttackStyle.SLASH, true), // tested bgs spec
    MELEE_BANDOS_GODSWORD_OR_SPEC(7643, AttackStyle.SLASH, true), // UNTESTED, assumed due to ags(or)
    MELEE_ARMADYL_GODSWORD_SPEC(7644, AttackStyle.SLASH, true), // tested ags spec
    MELEE_ARMADYL_GODSWORD_OR_SPEC(7645, AttackStyle.SLASH, true), // tested ags(or) spec
    MELEE_SCYTHE(8056, AttackStyle.SLASH), // tested w/ all scythe styles (so could be crush, but unlikely)
    MELEE_GHAZI_RAPIER_STAB(8145, AttackStyle.STAB), // rapier slash is 390, basic slash animation. Also VLS stab.

    // RANGED
    RANGED_SHORTBOW(426, AttackStyle.RANGED), // Confirmed same w/ 3 types of arrows, w/ maple, magic, & hunter's shortbow, craw's bow, dbow, dbow spec
    RANGED_RUNE_KNIFE_PVP(929, AttackStyle.RANGED), // 1 tick animation, has 1 tick delay between attacks. likely same for all knives. Same for morrigan's javelins, both spec & normal attack.
    RANGED_MAGIC_SHORTBOW_SPEC(1074, AttackStyle.RANGED, true),
    RANGED_CROSSBOW_PVP(4230, AttackStyle.RANGED), // Tested RCB & ACB w/ dragonstone bolts (e) & diamond bolts (e)
    RANGED_BLOWPIPE(5061, AttackStyle.RANGED), // tested in PvP with all styles. Has 1 tick delay between animations in pvp.
    RANGED_DARTS(6600, AttackStyle.RANGED), // tested w/ addy darts. Seems to be constant animation but sometimes stalls and doesn't animate
    RANGED_BALLISTA(7218, AttackStyle.RANGED), // Tested w/ dragon javelins.
    RANGED_DRAGON_THROWNAXE_SPEC(7521, AttackStyle.RANGED, true),
    RANGED_RUNE_CROSSBOW(7552, AttackStyle.RANGED),
    RANGED_BALLISTA_2(7555, AttackStyle.RANGED), // tested w/ light & heavy ballista, dragon & iron javelins.
    RANGED_RUNE_KNIFE(7617, AttackStyle.RANGED), // 1 tick animation, has 1 tick delay between attacks. Also d thrownaxe
    RANGED_DRAGON_KNIFE(8194, AttackStyle.RANGED),
    RANGED_DRAGON_KNIFE_POISONED(8195, AttackStyle.RANGED), // tested w/ d knife p++
    RANGED_DRAGON_KNIFE_SPEC(8292, AttackStyle.RANGED, true),

    // MAGIC - uses highest base damage available when animations are re-used. No damage = 0 damage.
    // for example, strike/bolt/blast animation will be fire blast base damage, multi target ancient spells will be ice barrage.
    MAGIC_STANDARD_BIND(710, AttackStyle.MAGIC), // tested w/ bind, snare, entangle
    MAGIC_STANDARD_STRIKE_BOLT_BLAST(711, AttackStyle.MAGIC), // tested w/ bolt
    MAGIC_STANDARD_BIND_STAFF(1161, AttackStyle.MAGIC), // tested w/ bind, snare, entangle, various staves
    MAGIC_STANDARD_STRIKE_BOLT_BLAST_STAFF(1162, AttackStyle.MAGIC), // strike, bolt and blast (tested all spells, different weapons)
    MAGIC_STANDARD_WAVE_STAFF(1167, AttackStyle.MAGIC), // tested many staves
    MAGIC_STANDARD_SURGE_STAFF(7855, AttackStyle.MAGIC), // tested many staves
    MAGIC_ANCIENT_SINGLE_TARGET(1978, AttackStyle.MAGIC), // Rush & Blitz animations (tested all 8, different weapons)
    MAGIC_ANCIENT_MULTI_TARGET(1979, AttackStyle.MAGIC), // Burst & Barrage animations (tested all 8, different weapons)
    MAGIC_VOLATILE_NIGHTMARE_STAFF_SPEC(8532, AttackStyle.MAGIC); // assume 99 mage's base damage (does not rise when boosted).

    private static final Map<Integer, AnimationData> DATA;

    int animationId;
    public boolean isSpecial;
    public AttackStyle attackStyle;


    // Simple animation data constructor for all melee and range attacks
    AnimationData(int animationId, AttackStyle attackStyle)
    {
        if (attackStyle == null)
        {
            throw new InvalidParameterException("Attack Style must be valid for AnimationData");
        }
        this.animationId = animationId;
        this.attackStyle = attackStyle;
        this.isSpecial = false;
    }
    // Simple animation data constructor for all melee and range attacks w/ special
    AnimationData(int animationId, AttackStyle attackStyle, boolean isSpecial)
    {
        if (attackStyle == null)
        {
            throw new InvalidParameterException("Attack Style must be valid for AnimationData");
        }
        this.animationId = animationId;
        this.attackStyle = attackStyle;
        this.isSpecial = isSpecial;

    }

    static
    {
        ImmutableMap.Builder<Integer, AnimationData> builder = new ImmutableMap.Builder<>();

        for (AnimationData data : values())
        {
            // allow to skip animation detection by using 0 or less as the animation id.
            if (data.animationId <= 0) { continue; }
            builder.put(data.animationId, data);
        }

        DATA = builder.build();
    }

    public static AnimationData dataForAnimation(int animationId)
    {
        return DATA.get(animationId);
    }

    public static boolean isStandardSpellbookSpell(AnimationData animationData)
    {
        return (animationData == MAGIC_STANDARD_STRIKE_BOLT_BLAST_STAFF ||
                animationData == MAGIC_STANDARD_WAVE_STAFF ||
                animationData == MAGIC_STANDARD_SURGE_STAFF);
    }

    public static boolean isFireSpell(AnimationData animationData)
    {
        return (animationData == MAGIC_STANDARD_STRIKE_BOLT_BLAST_STAFF ||
                animationData == MAGIC_STANDARD_STRIKE_BOLT_BLAST ||
                animationData == MAGIC_STANDARD_WAVE_STAFF ||
                animationData == MAGIC_STANDARD_SURGE_STAFF);
    }



    // An enum of combat styles (including stab, slash, crush).
    public enum AttackStyle
    {
        STAB(HeadIcon.MELEE, SpriteID.COMBAT_STYLE_SWORD_STAB),
        SLASH(HeadIcon.MELEE, SpriteID.COMBAT_STYLE_SWORD_SLASH),
        CRUSH(HeadIcon.MELEE, SpriteID.COMBAT_STYLE_HAMMER_POUND),
        RANGED(HeadIcon.RANGED, SpriteID.SKILL_RANGED),
        MAGIC(HeadIcon.MAGIC, SpriteID.SKILL_MAGIC);

        static AttackStyle[] MELEE_STYLES = {STAB, SLASH, CRUSH};

        public final HeadIcon protection;

        public final int styleSpriteId;

        AttackStyle(HeadIcon protection, int styleSpriteId)
        {
            this.protection = protection;
            this.styleSpriteId = styleSpriteId;
        }

        public boolean isMelee()
        {
            return ArrayUtils.contains(AttackStyle.MELEE_STYLES, this);
        }

        public boolean isUsingSuccessfulOffensivePray(Prayer pray)
        {
            return (
                    ((isMelee() &&
                            (pray == Prayer.PIETY ||
                                    pray == Prayer.CHIVALRY)) ||
                            (this == RANGED &&
                                    (pray == Prayer.RIGOUR ||
                                            pray == Prayer.EAGLE_EYE)) ||
                            (this == MAGIC &&
                                    (pray == Prayer.AUGURY ||
                                            pray == Prayer.MYSTIC_MIGHT)))
            );
        }


        @Override
        public String toString()
        {
            return StringUtils.capitalize(super.toString().toLowerCase());
        }
    }
}
