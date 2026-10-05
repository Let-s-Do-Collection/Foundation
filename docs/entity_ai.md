# Mob AI (`net.satisfy.foundation.entity.ai`)

## Attack with animation

The hit lands in the middle of the swing instead of the first tick:

```java
public class Bear extends PathfinderMob implements AttackAnimationMob {
    @Override public LivingEntity getAttackTarget() { return getTarget(); }
    @Override public void setAttacking(boolean attacking) { entityData.set(ATTACKING, attacking); }
    @Override public void performAttack(LivingEntity target) { doHurtTarget(target); }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new AnimationAttackGoal(this, 1.2, true, 20, 10)); // 20 tick swing, hit at tick 10
    }
}
```

## Random idle actions

```java
public class Squirrel extends Animal implements RandomAction {
    @Override public float chance() { return 0.002F; }
    @Override public int duration() { return 40; }
    @Override public boolean isPossible() { return onGround() && !isInWater(); }
    @Override public boolean isInterruptable() { return true; }
    @Override public void onStart() { entityData.set(SNIFFING, true); }
    @Override public void onStop() { entityData.set(SNIFFING, false); }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(5, new RandomActionGoal(this));
    }
}
```

The mob stands still while the action runs, unless `canMove()` returns `true`. `onTick(int tick)` is called every tick.

The method names deliberately differ from vanilla ones (`getAttackTarget`, not `getTarget`), otherwise they break after remapping in production.
