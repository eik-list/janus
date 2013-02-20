package de.mslab.applications;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import de.mslab.ciphers.CipherFactory;
import de.mslab.ciphers.CipherFactory.CipherName;
import de.mslab.ciphers.RoundBasedBlockCipher;
import de.mslab.ciphers.TweakableCipher;
import de.mslab.core.ByteArray;
import de.mslab.utils.Logger;

/**
 * Abstract base class for the exported applications of the framework.
 * @see BicliqueFinderApplication
 * @see MatchingApplication 
 */
abstract class AbstractApplication {
	
	protected RoundBasedBlockCipher cipher;
	protected Logger logger = Logger.getLogger();
	protected Options options = new Options();
	
	/**
	 * The constructor is to be called by the inherited concrete applications with the 
	 * arguments. This method will parse the arguments, create a {@code CommandLine}
	 * option from it, call the {@link #setUp(CommandLine)} method with the {@code CommandLine}
	 * instance, call the {@link #run()} method, and call the {@link #tearDown()} method.
	 * 
	 * If the {@link #setUp(CommandLine)} method call will throw an {@code MissingOptionException}
	 * error, {@link #logHelp(Options)} will be called. If another exception will be thrown
	 * by any called method, then the error will be caught and the stack trace printed. 
	 * @param args The program arguments.
	 */
	protected AbstractApplication(String[] args) {
		try {
			CommandLine commandLine = parseArguments(args);
			setUp(commandLine);
			run();
			tearDown();
		} catch (MissingOptionException e) {
			System.err.println(e.getMessage());
			logHelp(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The inherited concrete applications should implement their individual setup 
	 * routine in this method.
	 * @param commandLine A commandLine object that was created before from the 
	 * program arguments.
	 */
	public abstract void setUp(CommandLine commandLine);
	
	/**
	 * The inherited concrete applications should implement their individual main 
	 * functionality in this method.
	 * @throws Exception
	 */
	public abstract void run() throws Exception;
	
	/**
	 * The inherited concrete applications should implement their individual teardown 
	 * routine functionality in this method.
	 */
	public abstract void tearDown();
	
	/**
	 * Comfort function to return the String value of the specified option from the given
	 * {@code CommandLine} value. Returns the given {@code defaultValue} if the option was
	 * not specified.  
	 * @param commandLine The {@code CommandLine} object.
	 * @param optionName The name of the option.
	 * @param defaultValue A default value that is returned when the option was not specified.
	 * @return The actual value or the default value.
	 */
	protected String getOptionValue(CommandLine commandLine, String optionName, String defaultValue) {
		if (commandLine.hasOption(optionName)) {
			String value = commandLine.getOptionValue(optionName);
			return value == null ? 
				defaultValue: 
				value;
		} else {
			return defaultValue;
		}
	}
	
	/**
	 * Always asks the given {@code CommandLine} for the specified option. 
	 * @param commandLine The {@code CommandLine} object.
	 * @param optionName The name of the option.
	 * @return The value that was specified by the user as program argument for the given option.
	 * Will be {@code null}, if the user did not specify.
	 */
	protected String getRequiredOption(CommandLine commandLine, String optionName) {
		return commandLine.getOptionValue(optionName);
	}
	
	/**
	 * The inherited concrete classes must implement their help in this method.
	 * This method is called in the {@link #AbstractApplication(String[])} constructor 
	 * if a required program argument was not specified by the user.  
	 * @param options The user specified program arguments. 
	 */
	protected abstract void logHelp(Options options);
	
	/**
	 * Creates a {@code CommandLine} object from the given program arguments. 
	 * @param args The user-specified program arguments.
	 * @return An {@code CommandLine} object.
	 * @throws ParseException If the arguments could not be parsed due to some error.
	 */
	protected CommandLine parseArguments(String[] args) throws ParseException {
		options = new Options();
		createOptions();
		CommandLineParser parser = new PosixParser();
		return parser.parse(options, args);
	}
	
	/**
	 * This method is called from {@link #parseArguments(String[])} at the construction
	 * process. The inherited concrete classes should use this method to define/add their 
	 * program options in the {@link #options} member.   
	 */
	protected abstract void createOptions();
	
	/**
	 * Comfort function to create a program option. 
	 * @param opt The name of the option.
	 * @param description The description which will be displayed in the help log.
	 * @return The constructed option.
	 */
	protected Option createOption(String opt, String description) {
		return new Option(opt, description);
	}
	
	/**
	 * Comfort function to create a program option.
	 * @param opt The name of the option.
	 * @param description The description which will be displayed in the help log.
	 * @param required A flag which indicates whether or not this option will be required
	 * to be specified by the users.
	 * @return The constructed option.
	 */
	protected Option createOptionWithArg(String opt, String description, boolean required) {
		Option option = new Option(opt, true, description);
		option.setRequired(required);
		return option;
	}
	
	/**
	 * For biclique search, the user needs to specify the desired cipher to investigate.
	 * This comfort function returns a log output of all valid ciphers in case the user 
	 * named a non-defined cipher.
	 * @return A log output of all valid ciphers.
	 */
	protected String getCipherNames() {
		CipherName[] enumValues = CipherName.values();
		String cipherNames = "  ";
		String cipherName;
		
		for (int i = 0; i < enumValues.length; i++) {
			if (i != 0 && i % 5 == 0) {
				cipherNames += "\n  ";
			}
			
			cipherName = enumValues[i].toString();
			cipherNames += cipherName;
			
			if (i + 1 < enumValues.length) {
				cipherNames += ", ";
			}
		}
		
		return cipherNames;
	}
	
	/**
	 * Instantiates the {@link #cipher} member with an instance of the cipher which relates
	 * to the given name. If the cipher is tweakable, an all-zero tweak is used.
	 * @param cipherName The name of the cipher.
	 */
	protected void initializeCipher(CipherName cipherName) {
		cipher = CipherFactory.createCipher(cipherName);
		
		if (cipher instanceof TweakableCipher) {
			ByteArray tweak = new ByteArray(((TweakableCipher)cipher).getTweakSize());
			((TweakableCipher)cipher).setTweak(tweak);
		}
	}
	
}
