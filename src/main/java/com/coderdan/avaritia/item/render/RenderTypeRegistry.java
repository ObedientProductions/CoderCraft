package com.coderdan.avaritia.item.render;

import net.minecraft.client.renderer.RenderType;

public class RenderTypeRegistry {

    private static final ThreadLocal<RenderType> CURRENT = new ThreadLocal<>();

    public static void setCurrent(RenderType type) {
        CURRENT.set(type);
    }

    public static RenderType getCurrent() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }

    public static RenderType capturing(RenderType base) {
        return new RenderType(base.toString(), base.format(), base.mode(), base.bufferSize(), base.affectsCrumbling(), base.sortOnUpload(), base::setupRenderState, base::clearRenderState) {
            @Override
            public void setupRenderState() {
                RenderTypeRegistry.setCurrent(this);
                super.setupRenderState();
            }

            @Override
            public void clearRenderState() {
                super.clearRenderState();
                RenderTypeRegistry.clear();
            }
        };
    }

}
