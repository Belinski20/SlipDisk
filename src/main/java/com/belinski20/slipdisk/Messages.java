package com.belinski20.slipdisk;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class Messages {

    public static TextComponent makeComponent(String message)
    {
        return Component.text().content(message).build();
    }

    public static TextComponent makeComponent(String message, TextColor color)
    {
        return Component.text().content(message).color(color).build();
    }

    private static TextComponent makeSlipDiskPrefix()
    {
        return makeComponent("[").append(makeComponent("Slipdisk", NamedTextColor.AQUA)).append(makeComponent("] "));
    }

    public static TextComponent slipSetPrivate()
    {
        return makeSlipDiskPrefix().append(makeComponent("Status : ").append(makeComponent("Private", NamedTextColor.RED)));
    }

    public static TextComponent slipSetPublic()
    {
        return makeSlipDiskPrefix().append(makeComponent("Status : ").append(makeComponent("Public", NamedTextColor.GREEN)));
    }

    public static TextComponent untrustRemoveCantFind(String name)
    {
        return makeSlipDiskPrefix().append(makeComponent("Can't find " + name));
    }

    public static TextComponent untrustMissingArgument()
    {
        return makeSlipDiskPrefix().append(makeComponent("/slip untrust <player>"));
    }

    public static TextComponent trustHasNotPlayed(String name)
    {
        return makeSlipDiskPrefix().append(makeComponent(name + " has not played before or is an invalid name"));
    }

    public static TextComponent trustAlreadyTrusted(String name)
    {
        return makeSlipDiskPrefix().append(makeComponent(name + " is already trusted."));
    }

    public static TextComponent trustAdded(String name)
    {
        return makeSlipDiskPrefix().append(makeComponent(name + " was added to your trust list."));
    }

    public static TextComponent trustRemoved(String name)
    {
        return makeSlipDiskPrefix().append(makeComponent(name + " was removed from your trust list."));
    }

    public static TextComponent needProfileToUseCommand()
    {
        return makeSlipDiskPrefix().append(makeComponent("You need to place a Slip before using this command."));
    }

    public static TextComponent invalidCommandFormat()
    {
        return makeSlipDiskPrefix().append(makeComponent("Not a valid command format."));
    }

    public static TextComponent cannotTrustYourself()
    {
        return makeSlipDiskPrefix().append(makeComponent("You cannot add yourself to the trust list."));
    }

    public static TextComponent playerNotInTrustedList(String name)
    {
        return makeSlipDiskPrefix().append(makeComponent(name + " is not trusted"));
    }

    public static TextComponent privateSlip()
    {
        return makeSlipDiskPrefix().append(makeComponent("This Slip is private"));
    }


}
