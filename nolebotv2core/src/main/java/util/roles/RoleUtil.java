package util.roles;

import com.google.gson.*;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@NoArgsConstructor
public class RoleUtil extends ListenerAdapter
{
    private static final Logger logger = LogManager.getLogger(RoleUtil.class);
    public static ArrayList<Role> roles = new ArrayList<Role>();
    public static Map<String, Integer> roleMap = new HashMap<String, Integer>();
    private static File roleFile;
    private JDA jda;

    public void addNewRole(Role roleToAdd) {
        addRole(roleToAdd);
        saveRolesToJson();

    }
    // I am unsure when the ReadyEvent fires, I think it may be necessary to change this to a different event that fires later so roles may be read from config first
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        jda = event.getJDA();
        List<Guild> guilds = event.getJDA().getGuilds();
        readRolesFromJson();
        readRolesFromGuilds(guilds);
        saveRolesToJson();

    }
    private void addRole(Role roleToAdd) {
        roles.add(roleToAdd);
        roleMap.put(roleToAdd.getRoleName(), roles.size()-1);
        List<Guild> guilds = jda.getGuilds();
        guilds.stream().forEach(guild -> {
            RoleAction newRole = guild.createRole();
            newRole.setColor(roleToAdd.getRoleColor());
            newRole.setName(roleToAdd.getRoleName());
        });
    }
    //Using sneaky throws here because it complains about the file already existing in the final catch. Since we already checked for the file existing, this should never happen
    @SneakyThrows
    private void readRolesFromJson() {
        FileReader reader = null;
        try {
            Path current = Paths.get("roles.json");
            String s = current.toAbsolutePath().toString();
            roleFile = new File(s);
            reader = new FileReader(roleFile);
        }
       catch (IOException e) {
           logger.warn("Couldn't find role file, checking if it can be found in a development environment");
           try {
               Path current = Paths.get("src/main/resources/roles.json");
               String s = current.toAbsolutePath().toString();
               roleFile = new File(s);
               reader = new FileReader(roleFile);
           }
           catch (IOException ex) {
                logger.warn("Couldn't find role file, it will be created");
                roleFile = new File("roles.json");
                roleFile.createNewFile();
                reader = new FileReader(roleFile);
           }
       }
        JsonElement input = JsonParser.parseReader(reader);
        if (input.isJsonArray())
        {
            JsonArray jsonRoleArray = input.getAsJsonArray();
            // Can't use stream here, assume Gson doesn't support them. Could probably make a list of JsonObjects but I see little reason to
            for (JsonElement e : jsonRoleArray)
            {
                JsonObject o = e.getAsJsonObject();
                String roleName = o.get("name").getAsString();
                int permLevel = o.get("permLevel").getAsInt();
                Color roleColor = Color.decode(o.get("color").getAsString());
                boolean isAssignable = o.get("assignable").getAsBoolean();
                Role r = new Role(roleName, permLevel, isAssignable, roleColor);
                addRole(r);
            }
        }



    }
    private void readRolesFromGuilds(List<Guild> guilds)
    {
        guilds.stream().forEach((guild) -> {
            List<net.dv8tion.jda.api.entities.Role> guildRoles = guild.getRoles();
            guildRoles.stream().forEach(role -> {
                if (!roleMap.containsKey(role.getName()))
                {
                    logger.info("Added existing role " + role.getName());
                    addRole(new Role(role.getName(), -1000, role.getColor()));
                }
            });
        });
    }
    private static void saveRolesToJson()
    {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Gson gson = builder.create();
        FileWriter writer = null;
        try {
            writer = new FileWriter(roleFile);
        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
        }
        JsonArray outputFile = new JsonArray();
        roles.stream().forEach((role -> {
            JsonObject o = new JsonObject();
            o.addProperty("name", role.getRoleName());
            o.addProperty("permLevel", role.getPermLevel());
            //Not gonna lie, I found this code on Stack Overflow. Should save a color as a hexcode
            o.addProperty("color", "#FFFFFF");
            o.addProperty("assignable", role.isAssignable());
            outputFile.add(o);
        }));
        try {
            writer.write(gson.toJson(outputFile));
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
        }


    }
}
