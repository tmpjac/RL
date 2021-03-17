package net.runelite.client.plugins.dps;

public class AttackTuple {

    public int _weaponId;
    public int _attackSpeed;
    public String _enemyId;
    //public String _attackType;
    public String _prayer;

    public AttackTuple(int weaponId, String enemyId, int attackSpeed, String prayer){
        _weaponId = weaponId;
        _enemyId = enemyId;
        //_attackType = attackType;
        _attackSpeed = attackSpeed;
        _prayer = prayer;
    }

    @Override
    public String toString(){
        return "WeaponId:" + _weaponId + ",EnemyId:" + _enemyId + ",Prayer:" + _prayer;
    }

    @Override
    public boolean equals(Object obj){

        if (obj == null || obj.getClass() != this.getClass()){
            return false;
        }

        AttackTuple at2 = (AttackTuple)obj;

        if (this._weaponId == at2._weaponId && _enemyId.equals(at2._enemyId)  && _attackSpeed == at2._attackSpeed && _prayer.equals(at2._prayer)){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return (int) _weaponId * _enemyId.hashCode() * _attackSpeed * _prayer.hashCode() ;
    }


}
