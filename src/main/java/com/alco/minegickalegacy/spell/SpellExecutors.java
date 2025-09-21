package com.alco.minegickalegacy.spell;

import com.alco.minegickalegacy.mechanics.Element;
import com.alco.minegickalegacy.registry.MagickRegistry.MagickDefinition;

import java.util.List;
import java.util.function.Function;

public final class SpellExecutors {
    private SpellExecutors() {
    }

    public static SpellForm determineForm(final List<Element> combination) {
        if (combination.stream().anyMatch(element -> element == Element.SHIELD)) {
            return SpellForm.GROUND;
        }
        if (combination.stream().anyMatch(element -> element == Element.EARTH || element == Element.ICE)) {
            return SpellForm.PROJECTILE;
        }
        if (combination.stream().anyMatch(element -> element == Element.ARCANE || element == Element.LIFE)) {
            return SpellForm.BEAM;
        }
        if (combination.stream().anyMatch(element -> element == Element.LIGHTNING)) {
            return SpellForm.LIGHTNING;
        }
        if (!combination.isEmpty()) {
            return SpellForm.SPRAY;
        }
        return SpellForm.UNKNOWN;
    }

    public static SpellExecutor createExecutor(final SpellForm form, final MagickDefinition definition) {
        return switch (form) {
            case BEAM -> new SustainedSpellExecutor(true, 2, SpellExecutors::defaultSustainDuration, SpellExecutors::beamSustainCost);
            case LIGHTNING -> new SustainedSpellExecutor(true, 4, SpellExecutors::defaultSustainDuration, SpellExecutors::lightningSustainCost);
            case SPRAY -> new SustainedSpellExecutor(true, 1, SpellExecutors::defaultSustainDuration, SpellExecutors::spraySustainCost);
            case PROJECTILE -> new InstantSpellExecutor();
            case GROUND -> new InstantSpellExecutor();
            case UNKNOWN -> new InstantSpellExecutor();
        };
    }

    private static int defaultSustainDuration(final SpellContext context) {
        return 75 + context.elementCount() * 25;
    }

    private static double beamSustainCost(final SpellContext context) {
        return SpellAttributes.sustainCost(context, 2.2D);
    }

    private static double lightningSustainCost(final SpellContext context) {
        return SpellAttributes.sustainCost(context, 2.5D);
    }

    private static double spraySustainCost(final SpellContext context) {
        final int elements = context.elementCount();
        final double consume = Math.max(0.1D, context.staffStats().consume());
        final double factor = Math.pow(elements, 1.2D) * 1.5D;
        return consume * factor;
    }

    private static final class InstantSpellExecutor implements SpellExecutor {
        @Override
        public boolean onStart(final SpellContext context) {
            context.performEffect();
            return false;
        }

        @Override
        public boolean tick(final SpellContext context) {
            return false;
        }

        @Override
        public void onStop(final SpellContext context, final SpellStopReason reason) {
        }
    }

    private static final class SustainedSpellExecutor implements SpellExecutor {
        private final boolean triggerOnStart;
        private final int interval;
        private final Function<SpellContext, Integer> durationFunction;
        private final Function<SpellContext, Double> costFunction;

        private SustainedSpellExecutor(final boolean triggerOnStart,
                                       final int interval,
                                       final Function<SpellContext, Integer> durationFunction,
                                       final Function<SpellContext, Double> costFunction) {
            this.triggerOnStart = triggerOnStart;
            this.interval = Math.max(1, interval);
            this.durationFunction = durationFunction;
            this.costFunction = costFunction;
        }

        @Override
        public boolean onStart(final SpellContext context) {
            if (triggerOnStart) {
                context.performEffect();
            }
            return durationFunction.apply(context) > 0;
        }

        @Override
        public boolean tick(final SpellContext context) {
            final int maxDuration = durationFunction.apply(context);
            if (maxDuration > 0 && context.ticks() >= maxDuration) {
                return false;
            }
            final double sustainCost = Math.max(0.0D, costFunction.apply(context));
            if (sustainCost > 0.0D && !context.consumeMana(sustainCost, context.ticks() % interval == 0)) {
                return false;
            }
            if (context.ticks() % interval == 0) {
                context.performEffect();
            }
            return true;
        }

        @Override
        public void onStop(final SpellContext context, final SpellStopReason reason) {
        }
    }
}