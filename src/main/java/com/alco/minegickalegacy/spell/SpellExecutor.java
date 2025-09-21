package com.alco.minegickalegacy.spell;

public interface SpellExecutor {
    boolean onStart(SpellContext context);

    boolean tick(SpellContext context);

    void onStop(SpellContext context, SpellStopReason reason);
}
