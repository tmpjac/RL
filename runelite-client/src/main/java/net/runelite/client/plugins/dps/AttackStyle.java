package net.runelite.client.plugins.dps;

import net.runelite.api.Skill;

enum AttackStyle
{
    ACCURATE("Accurate", Skill.ATTACK),
    AGGRESSIVE("Aggressive", Skill.STRENGTH),
    DEFENSIVE("Defensive", Skill.DEFENCE),
    CONTROLLED("Controlled", Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE),
    ACCURATE_RANGING("Accurate", Skill.RANGED),
    RAPID("Rapid", Skill.RANGED),
    LONGRANGE("Longrange", Skill.RANGED, Skill.DEFENCE),
    CASTING("Casting", Skill.MAGIC),
    DEFENSIVE_CASTING("Defensive Casting", Skill.MAGIC, Skill.DEFENCE),
    ACCURATE_CASTING("Accurate", Skill.MAGIC),
    LONGRANGE_CASTING("Longrange", Skill.MAGIC, Skill.DEFENCE),
    AIM_AND_FIRE("Aim and Fire"),
    BLOCK("Blocking");

    private final String name;
    private final Skill[] skills;

    AttackStyle(String name, Skill... skills)
    {
        this.name = name;
        this.skills = skills;
    }

    public String getName()
    {
        return name;
    }

    public Skill[] getSkills()
    {
        return skills;
    }
}