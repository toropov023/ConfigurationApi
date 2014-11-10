package com.copperboard.ConfigurationApi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.*;

/**
* Example usage:
* <pre>
* {@code
*
* com.copperboard.ConfigurationApi.ConfigurationAPI.ConfigurationSection section = com.copperboard.ConfigurationApi.ConfigurationAPI.load("C:\\test.txt");
*
* int age = section.getInt("age");
* String name = section.getString("name");
*
* section.set("age", 10);
* section.set("name", "Bob");
*
* section.save();
* section.reload();
* }
* </pre>
*
* @see ConfigurationAPI.ConfigurationSection
*
* @author toropov023
* @version 1.0
 */
public class ConfigurationAPI {
    static private Map<String, ConfigurationSection> configs = new HashMap<>();

    /**
     * Loads a configuration file from the path specified. If the file doesn't exist it will be created automatically.
     * <br><br>
     * See {@link #load(String, boolean)}.
     * @param path Path to the file
     * @return Returns the {@link ConfigurationAPI.ConfigurationSection} instance. Either loaded from map or created a new one.
     */
    public static ConfigurationSection load(String path){
        return load(path, true);
    }

    /**
     * Loads a configuration file from the path specified.
     * @param path Path to the file
     * @param create If true then a new file will be created, false otherwise
     * @return Returns the {@link ConfigurationAPI.ConfigurationSection} instance. Either loaded from map or created a new one.
     */
    public static ConfigurationSection load(String path, boolean create){
        if(configs.containsKey(path)){
            return configs.get(path);
        } else {
            ConfigurationSection section = new ConfigurationSection(path, create);
            configs.put(path, section);
            return section;
        }
    }

    /**
     * ConfigurationSection class. Responsible for keeping a map of values loaded from a file.
     *
     * @see #getInt(String)
     * @see #getFloat(String)
     * @see #getDouble(String)
     * @see #getLong(String)
     * @see #getBoolean(String)
     * @see #getString(String)
     *
     * @see #set(String, Object)
     *
     * @see #save()
     * @see #reload()
     */
    static public class ConfigurationSection{
        private Map<String, String> map = new HashMap<>();
        private Path path;

        /**
         * ConfigurationSection instance
         * @param path Path to the file
         * @param create If true then a new file will be created, false otherwise
         */
        public ConfigurationSection(String path, boolean create){
            this.path= Paths.get(path);
            reload(create);
        }

        /**
         * Reload the configuration instance emptying the map and loading the values from the file again. If the file doesn't exist it will be created automatically.
         */
        public void reload(){
            reload(true);
        }

        /**
         * Reload the configuration instance emptying the map and loading the values from the file again.
         * @param create If true then a new file will be created, false otherwise
         */
        public void reload(boolean create){
            map.clear();
            if(!Files.exists(path)){
                if(create) {
                    try {
                        Files.createFile(path, new FileAttribute<?>[0]);
                    } catch (IOException e) {
                        System.out.print("[com.copperboard.ConfigurationApi.ConfigurationAPI] Error creating a file in: " + path.toUri());
                    }
                }else
                    return;
            }

            try {
                Scanner scanner =  new Scanner(path);
                while (scanner.hasNextLine()){
                    processLine(scanner.nextLine());
                }
            } catch (IOException e){
                System.out.print("[com.copperboard.ConfigurationApi.ConfigurationAPI] Error loading a file from: "+path.toUri());
            }
        }
        private void processLine(String aLine){
            //use a second Scanner to parse the content of each line
            Scanner scanner = new Scanner(aLine);
            scanner.useDelimiter(":");
            if (scanner.hasNext())
                map.put(scanner.next().trim(), scanner.next().trim());
        }

        /**
         * Save the Configuration to a file. A new file will be created if one doesn't exist.
         */
        public void save(){
            List<String> list = new ArrayList<>();
            for(String key : map.keySet())
                list.add(key+": "+map.get(key));

            try {
                Files.write(path, list);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        //Getting methods

        /**
         * Get Boolean value
         * @param path Key to the value
         * @return The value or false if doesn't exist
         */
        public boolean getBoolean(String path){
            String obj = map.get(path);
            return obj != null && Boolean.parseBoolean(obj);
        }

        /**
         * Get Double value
         * @param path Key to the value
         * @return The value or 0 if doesn't exist
         */
        public double getDouble(String path){
            String obj = map.get(path);
            try {
                return obj == null ? 0 : Double.parseDouble(obj);
            }catch (NumberFormatException e){
                System.out.print("[com.copperboard.ConfigurationApi.ConfigurationAPI] Couldn't parse Doubke from \""+obj+"\"");
                return 0;
            }
        }

        /**
         * Get Float value
         * @param path Key to the value
         * @return The value or 0 if doesn't exist
         */
        public float getFloat(String path){
            String obj = map.get(path);
            try {
                return obj == null ? 0 : Float.parseFloat(obj);
            }catch (NumberFormatException e){
                System.out.print("[com.copperboard.ConfigurationApi.ConfigurationAPI] Couldn't parse Float from \""+obj+"\"");
                return 0;
            }
        }

        /**
         * Get Long value
         * @param path Key to the value
         * @return The value or 0 if doesn't exist
         */
        public long getLong(String path){
            String obj = map.get(path);
            try {
                return obj == null ? 0 : Long.parseLong(obj);
            }catch (NumberFormatException e){
                System.out.print("[com.copperboard.ConfigurationApi.ConfigurationAPI] Couldn't parse Long from \""+obj+"\"");
                return 0;
            }
        }

        /**
         * Get Int value
         * @param path Key to the value
         * @return The value or 0 if doesn't exist
         */
        public int getInt(String path){
            String obj = map.get(path);
            try {
                return obj == null ? 0 : Integer.parseInt(obj);
            }catch (NumberFormatException e){
                System.out.print("[com.copperboard.ConfigurationApi.ConfigurationAPI] Couldn't parse Integer from \""+obj+"\"");
                return 0;
            }
        }

        /**
         * Get String value
         * @param path Key to the value
         * @return The value or null if doesn't exist
         */
        public String getString(String path){
            return map.get(path);
        }

        //Setting methods

        /**
         * Sets a value to a specified key.
         * <br><br>
         *     See {@link ConfigurationAPI.ConfigurationSection#save()} to save the ConfigurationSection after editing any values.
         * @param key Key to the value
         * @param value Object to be set
         */
        public void set(String key, Object value){
            map.put(key, value.toString());
        }

        //Reset

        /**
         * Reset the ConfigurationSection emptying the map.
         */
        public void destroy(){
            map.clear();
        }
    }
}
