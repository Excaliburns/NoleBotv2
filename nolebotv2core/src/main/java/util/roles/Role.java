package util.roles;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
@Setter @Getter
public class Role
{
    private String roleName;
    private int permLevel;
    private boolean isAssignable;
    private Color roleColor;

    public Role(String name, int permission, boolean assignable, Color c) {
        roleName = name;
        permLevel = permission;
        isAssignable = assignable;
        roleColor = c;
    }
    public Role(String name, int permission, boolean assignable) {
        roleName = name;
        permLevel = permission;
        isAssignable = assignable;
        roleColor = Color.GRAY;
    }
    public Role(String name, int permission,  Color c) {
        roleName = name;
        permLevel = permission;
        isAssignable = true;
        roleColor = c;
    }
    public Role(String name, int permission) {
        roleName = name;
        permLevel = permission;
        isAssignable = true;
        roleColor = Color.GRAY;
    }
}
