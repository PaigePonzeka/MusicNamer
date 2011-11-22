import java.io.File;
import java.util.regex.*;



public class MusicNamer{

  /**
    Main program
  **/
  public static void main(String[] argv) {

    /******* Create a new directory lister ********/
    /* For each directory, list the files */

    DirectoryTree directory = new DirectoryTree();
    if (argv.length == 0)
      directory.listFiles(".");
    else
      for (int i = 0; i < argv.length; i++)
        directory.listFiles(argv[i]);

  }
}


/*
  Iterates over a directory and all children directories
*/
class DirectoryTree {
  // debug boolean for system.out.print statements
  public static final boolean DEBUG = true;

  /** doDir - handle one filesystem object by name */
  public void listFiles(String s) {
    File f = new File(s);
    if (!f.exists()) {
      return;
    }
    if (f.isFile())
      processFile(f);
    else if (f.isDirectory()) {
      String objects[] = f.list();

      for (int i = 0; i < objects.length; i++)
        listFiles(s + f.separator + objects[i]);
    } else
      System.err.println("Unknown: " + s);
  }

  /**
    Process one regular file.
  **/
  public static void processFile(File f) {
    String file_path = f.getPath();
    String file_name = f.getName();
    String file_directory = file_path.replace(file_name, "");
    // TODO need to take into account files without a file type
    String new_file_name = file_directory + processFileName(f);
    File new_file = new File( new_file_name );

    // not going to rename files in debug mode - just a coding convience
    if (DEBUG)
      {
        System.out.println( "New File Name: " + new_file_name);
      }
    else
      {
        f.renameTo(new_file);
      }
  }

  /**
      Returns a String of the extension
      takes filename.m4a and returns .m4a
  **/
  public static String getFileType(String file_name)
  {
    // TODO - need to take into account the files without a file type
    String[] result = file_name.split("\\.");
    return "." + result[result.length - 1];
  }

  /**
      Process File names removes
      01 - song name.m4v
      01 song name.m4v
      Album - song name.m4v
      Album song name.m4v
      song name (album artist version).m4v

      assuming "artist/album/song' structure
  **/
  public static String processFileName(File file)
  {
    String file_album = file.getParent();
    String file_artist = file.getParentFile().getParent();
    String file_name = file.getName();

    if(DEBUG)
    {
      System.out.println(" \n File Name:" + file_name +
                      "\t File Album: " + file_album +
                      "\t File Artist: " + file_artist);
    }

    file_name = removeContent(file_name, file_album);
    file_name = removeContent(file_name, file_artist);

    if(DEBUG)
       {
         System.out.println("Processed File Name:" + file_name);
       }
    return file_name;
  }

  /**
      Checks and removes content from the file_name, returns updated filename

      currently Supporting:
        (content) (done)
        content (done)
        Content (done)
        CoNTeNt (done)

      currently unsupported but on the list:
        (content some more text)
        content - (done)
  **/
  public static String removeContent(String file_name, String remove_content)
  {
    // make sure the content exists
    if (remove_content != null)
    {
      // Support word inside parenthesis inside parenthesis
      String regex_pattern_parenthesis = "[\\(]" + remove_content + "[\\)]";
     // Supports word at beginning or end or surrounded by spaces
     String regex_pattern = "\\b"+ remove_content + "\\b";
     //Remove beginning Track numbers
        // TODO Take into account songs with numbers in the title
     String regex_track_numbers = "^\\d++";
     // remove left over punctuation at the beginning of the track name
     String regex_cleaning = "^[\\s[^\\w]]++";

       file_name = runRegex(file_name, regex_pattern_parenthesis);
       file_name = runRegex(file_name, regex_pattern);
       file_name = runRegex(file_name, regex_track_numbers);
       file_name = runRegex(file_name, regex_cleaning);
    }

    return file_name;
  }

  /*
    Runs a given regex on a given string
    Removing the matching content
  */
  public static String runRegex(String file_name, String regex)
  {
      // TODO add support for additional content inside parenthesis
      Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(file_name);

      if (DEBUG)
      {
        System.out.println("REGEX: "+regex);
        boolean found = false;
        while (matcher.find()) {
            System.out.println("I found the text "+matcher.group() +
            " starting at " + "index "+matcher.start()+
            " and ending at index "+matcher.end());
            found = true;
        }
        if(!found){
            System.out.println("No match found.");
        }
      }

      // remove the text
      file_name = matcher.replaceAll("");
      file_name = file_name.trim();

      return file_name;
    }
}