package com.search.searchEngine.Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.search.searchEngine.Model.UserForm;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	  @Autowired
	    private ServletContext servletContext;

	static HashMap<String, HashMap<String, Integer>> wordFileMapping=new HashMap<String, HashMap<String, Integer>>();
	final String sourcefolderName="W3CWebPages";
	final String sourcefolderFilesExtension=".htm";
	final String targetfolderName="Text";
	@RequestMapping(value = "/")
	public ModelAndView firstPage(ModelAndView model, HttpServletRequest request) throws IOException {
		
		boolean isInitialized =false;
		if(null!=servletContext.getAttribute("isInitialized") && true== (boolean)servletContext.getAttribute("isInitialized"))
		{
			isInitialized=true;
		}
		else
		{
			Initialization();
			isInitialized=true;
		}
		
		UserForm userform = new UserForm();
		model.addObject("userform", userform);
		servletContext.setAttribute("isInitialized", isInitialized);
		model.setViewName("home");
		return model;
	}
	
	@RequestMapping(value = "/submitForm")
	public ModelAndView submitForm(ModelAndView model, @Valid @ModelAttribute("userform") UserForm userform, BindingResult result,HttpServletRequest request) throws IOException {
		
		if (result.hasErrors()) {
			System.out.println("Error in submit form Controller");
		}

		
		HashMap<String, Integer> documentWordFrequency=getMapForInput(userform.getInputData(),request);	
		model.addObject("userform", userform);
		model.addObject("from", "results");
		model.addObject("documentWordFrequency", documentWordFrequency);
		model.setViewName("home");
		return model;
	}

	@RequestMapping(value = "/wordSuggestion")
	@ResponseBody
	public  String getsuggetionForUI(@RequestParam String  word,ModelAndView model,@Valid @ModelAttribute("userform") UserForm userform, BindingResult result,HttpServletRequest request) {
		if (result.hasErrors()) {
			System.out.println("Error in submit form Controller");
		}
		List<String> l = new ArrayList<String>();
		Map<String,Integer> m = new HashMap<String, Integer>();
		int distance;
		HashMap<String, HashMap<String, Integer>> wordFileMappingForLocal=(HashMap<String, HashMap<String, Integer>>) servletContext.getAttribute("wordFileMapping");
		for(String keys : wordFileMappingForLocal.keySet()) {
			if(keys.contains(word.trim().toLowerCase())) {
				l.add(keys);
				distance = editDistance(word.trim().toLowerCase(),keys);
				m.put(keys,distance);
			}
		}
		ArrayList<String> words =sortmap(m);
		String wordsformat=words.toString().replaceAll("\\[", "");
		wordsformat=wordsformat.replaceAll("\\]", "");
		return wordsformat;
	}
	
	@RequestMapping(value = "/getFile")
	@ResponseBody
	public String getFile(@RequestParam String  filename,ModelAndView model,@Valid @ModelAttribute("userform") UserForm userform, BindingResult result, HttpServletRequest request) throws IOException {
		
		if (result.hasErrors()) {
			System.out.println("Error in submit form Controller");
		}
		String filePath = getFilePath(filename);
		
		return filePath;
	}
	public String getFilePath(String filename)
	{
		
		ClassLoader classLoader = getClass().getClassLoader();
		URL url = classLoader.getResource(sourcefolderName+"/"+filename+sourcefolderFilesExtension);
		
		
		return url.toString();
		
	}
	public HashMap<String, Integer>  getMapForInput(String inputData,HttpServletRequest request)
	{
		HashMap<String, Integer> documentWordFrequency=new HashMap<String, Integer>();
		HashMap<String, HashMap<String, Integer>> wordFileMappingForLocal=(HashMap<String, HashMap<String, Integer>>) servletContext.getAttribute("wordFileMapping");
		if(!inputData.isEmpty())
		documentWordFrequency=wordFileMappingForLocal.get(inputData.trim().toLowerCase());
		Map<String,Integer> map=null;
		if(null!=documentWordFrequency)
		{
			 map = 
				     documentWordFrequency.entrySet().stream().sorted(java.util.Map.Entry.comparingByValue(Comparator.reverseOrder()))
				     .collect(Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue,
				                              (e1, e2) -> e1, LinkedHashMap::new));	
		}
		return (HashMap<String, Integer>) map;	
		
	}
	
	
	
	
	
	
	
	
	public void Initialization() {
		HtmlToText(sourcefolderName, targetfolderName);
		creatingIndex(targetfolderName,wordFileMapping,3);
		
		
		HashMap<String, Integer> documentWordFrequency=new HashMap<String, Integer>();
		documentWordFrequency=wordFileMapping.get("participate");
		///System.out.println("OutPut: "+documentWordFrequency);
		int count=0;
		for (HashMap.Entry<String, Integer> entry : documentWordFrequency.entrySet())
		{
		    System.out.println(entry.getKey() + "-----" + entry.getValue());
		    count+=entry.getValue();
		}
		System.out.println("OutPut:count: "+count);
		
		
		
	}

	public void HtmlToText(String folderName, String outputFolderName) {
		ClassLoader classLoader = getClass().getClassLoader();
		URL url = classLoader.getResource(folderName);
		File folder = new File(url.getPath());
		StringBuilder fileString = null;
		for (File file : folder.listFiles()) {
			fileString = getFileContent(file);
			Document document = Jsoup.parse(fileString.toString());
			writingHtmlToText(document, file.getName(), outputFolderName);
		}

	}

	public StringBuilder getFileContent(File file) {
		// File filename = new File("Hard disk.txt");
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		StringBuilder fileString = new StringBuilder();
		while (sc.hasNextLine()) {
			fileString.append(sc.nextLine() + "\n");
		}
		sc.close();
		return fileString;
	}

	public void writingHtmlToText(Document document, String fileName, String outputFolderName) {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL url = classLoader.getResource(outputFolderName);
			File directory ;
			if(null==url)
				directory = new File(outputFolderName);
			else
			directory = new File(url.getPath());
			if (!directory.exists()) {
				directory.mkdir();
			}

			FileWriter fw = new FileWriter(
					directory + "\\" + fileName.substring(0, fileName.lastIndexOf(".")) + ".txt");
			fw.write(document.text());
			fw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public  void creatingIndex(String folderName,HashMap<String, HashMap<String, Integer>> wordFileMapping,int ignoreStringOfLength) {
		// File filename = new File("Hard disk.txt");
		ClassLoader classLoader = getClass().getClassLoader();
		URL url = classLoader.getResource(folderName);
		File folder = new File(url.getPath());
		for (File f : folder.listFiles())
		{
			Scanner sc = null;
			String fileName=f.getName().substring(0, f.getName().lastIndexOf("."));
			try {
				sc = new Scanner(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			while (sc.hasNextLine()) 
			{
				String currentLine = sc.nextLine();
				  currentLine=getDelimeterSeparatedWords(currentLine);
				  String[] words = currentLine.split(" ");
				  for(String word : words)
				  {
					  word=word.trim().toLowerCase();
					  if(!word.startsWith("http"))
					 {	  
					  if(word.length()>ignoreStringOfLength && wordFileMapping.containsKey(word))
			    	   {
						  HashMap<String, Integer>  documentWordFrequency =wordFileMapping.get(word);
						  if(documentWordFrequency.containsKey(fileName))
						  {
			    		   documentWordFrequency.put(fileName, documentWordFrequency.get(fileName)+1);
			    		   wordFileMapping.put(word, documentWordFrequency);
						  }
						  else
						  {
							  documentWordFrequency.put(fileName, 1);
				    		   wordFileMapping.put(word, documentWordFrequency);
						  }
			    	   }
			    	   else if(word.length()>ignoreStringOfLength)
			    	   {
			   			HashMap<String, Integer> documentWordFrequency=new HashMap<String, Integer>();
			   			   documentWordFrequency.put(fileName, 1);
			    		   wordFileMapping.put(word, documentWordFrequency);
			    	   }
					 }
					  
				  }
			///fileString.append(sc.nextLine() + "\n");
			}
			sc.close();
		}
		servletContext.setAttribute("wordFileMapping", wordFileMapping);
	}
	
	public  String getDelimeterSeparatedWords(String line)
	{
		line=line.replaceAll("-", " ");
		line=line.replaceAll("_", " ");
		line=line.replaceAll("\"", " ");
		line=line.replaceAll("\\(", " ");
		line=line.replaceAll("\\)", " ");
		line=line.replaceAll("\\[", " ");
		line=line.replaceAll("\\]", " ");
		line=line.replaceAll("\\{", " ");
		line=line.replaceAll("\\}", " ");
		line=line.replaceAll(",", " ");
		line=line.replaceAll(">", " ");
		line=line.replaceAll("<", " ");
		line=line.replaceAll(";", " ");
		line=line.replaceAll("\\.", " ");
		line=line.replaceAll(":", " ");
		line=line.replaceAll("\\?", " ");
		line=line.replaceAll("\\*", " ");
		line=line.replaceAll("#", " ");
		line=line.replaceAll("/", " ");
		line=line.replaceAll("'\'", " ");
		line=line.replaceAll("!", " ");
		line = line.replaceAll("“", " ");
		line = line.replaceAll("\\'", " ");
		line = line.replaceAll("=", " ");
		line = line.replaceAll("!", " ");
		line = line.replaceAll("\\`", " ");
		line = line.replaceAll("@", " ");
		line = line.replaceAll("\\~", " ");
		line = line.replaceAll("\\$", " ");
		line = line.replaceAll("\\^", " ");
		line = line.replaceAll("#", " ");
		line = line.replaceAll("\\+", " ");
		line=line.trim();
		return line;
	}
	
	public  int editDistance(String word1,String word2) {
		int len1 = word1.length();
		int len2 = word2.length();
			// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];
	 	for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
	 	for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}
	 
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}
	 
		return dp[len1][len2];
	}
	
	public ArrayList<String> sortmap(Map<String,Integer> m){
		ArrayList<String> words = new ArrayList<String>();
		Map<String,Integer> sortedMap = 
			     m.entrySet().stream().sorted(java.util.Map.Entry.comparingByValue())
			     .collect(Collectors.toMap(java.util.Map.Entry::getKey, java.util.Map.Entry::getValue,
			                              (e1, e2) -> e1, LinkedHashMap::new));	
		for (java.util.Map.Entry<String, Integer> entry : sortedMap.entrySet())
		{
		   //// System.out.println(entry.getKey() + "-->Distance" + entry.getValue());
		    words.add(entry.getKey() );
		}
		return words;
	}
	public  void getsuggetion(String word) {
		List<String> l = new ArrayList<String>();
		Map<String,Integer> m = new HashMap<String, Integer>();
		int distance;
		for(String keys : wordFileMapping.keySet()) {
			if(keys.contains(word)) {
				l.add(keys);
				distance = editDistance(word,keys);
				m.put(keys,distance);
			}
		}
		sortmap(m);
	}
	
	

}
