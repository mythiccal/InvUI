package xyz.xenondevs.inventoryaccess.component.i18n;

import net.kyori.adventure.text.*;

public class AdventureComponentLocalizer extends ComponentLocalizer<Component> {
    
    private static final AdventureComponentLocalizer INSTANCE = new AdventureComponentLocalizer();
    
    private AdventureComponentLocalizer() {
        super(Component::text);
    }
    
    public static AdventureComponentLocalizer getInstance() {
        return INSTANCE;
    }
    
    @Override
    public Component localize(String lang, Component component) {
        ComponentBuilder<?, ?> builder;
        if (component instanceof TranslatableComponent) {
            builder = localizeTranslatable(lang, (TranslatableComponent) component).toBuilder();
        } else {
            builder = component.toBuilder();
        }
        
        builder.mapChildrenDeep(child -> {
            if (child instanceof TranslatableComponent)
                return localizeTranslatable(lang, (TranslatableComponent) child);
            return child;
        });
        
        return builder.build();
    }
    
    private Component localizeTranslatable(String lang, TranslatableComponent component) {
        var formatString = Languages.getInstance().getFormatString(lang, component.key());
        if (formatString == null)
            return component;
        
        var args = component.arguments().stream()
            .map(TranslationArgument::asComponent)
            .toList();
        var children = decomposeFormatString(lang, formatString, component, args);
        return Component.textOfChildren(children.toArray(ComponentLike[]::new)).style(component.style());
    }
    
}
