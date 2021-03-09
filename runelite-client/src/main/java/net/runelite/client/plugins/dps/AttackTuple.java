package net.runelite.client.plugins.dps;

public class AttackTuple {

    public int _weaponId;
    public String _enemyId;
    public String _attackType;

    public AttackTuple(int weaponId, String enemyId, String attackType){
        _weaponId = weaponId;
        _enemyId = enemyId;
        _attackType = attackType;
    }

    @Override
    public String toString(){
        return "WeaponId:" + _weaponId + ",EnemyId:" + _enemyId + ",AttackType:" + _attackType;
    }

    @Override
    public boolean equals(Object obj){

        if (obj == null || obj.getClass() != this.getClass()){
            return false;
        }

        AttackTuple at2 = (AttackTuple)obj;

        if (this._weaponId == at2._weaponId){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return (int) _weaponId * _enemyId.hashCode() * _attackType.hashCode();
    }


}
