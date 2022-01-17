import static org.junit.Assert.*;

import java.util.*;
import java.util.stream.*;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.*;
import org.junit.runner.*;

public class InMemoryFileSystem {
	class FileSystemServer{
		class Entity{
			boolean isDirectory;
			String parentPath;
			String name;
			Map<String,Entity>child;
			String fileContent;
			public Entity(boolean isDirectory, String parentPath,String name){
				this.isDirectory=isDirectory;
				this.parentPath=parentPath;
				this.name=name;
				this.child=new HashMap<>();
			}
		}
		Map<String,Entity>entities;
		public FileSystemServer(){
			Entity root=new Entity(true,"","/");
			entities=new HashMap<>();
			entities.put("/", root);
		}

		Pair<String,String> splitFullPath(String path){
			int index = path.substring(0,path.length()-1).lastIndexOf("/");
			return Pair.of(path.substring(0,index+1),path.substring(index+1));
		}

		String getFullPath(String parentPath, String name){
			return parentPath+name;
		}

		public void createDirectory(String dirPath){
			Pair<String,String>paths=splitFullPath(dirPath);
			Entity parentNode=entities.get(paths.getKey());
			Entity node=new Entity(true,paths.getKey(),paths.getValue());
			entities.put(dirPath,node);
			parentNode.child.put(paths.getValue(),node);
		}
		public void createFile(String dirPath, String fileName, String data){
			Entity parentNode=entities.get(dirPath);
			Entity node=new Entity(false,dirPath,fileName);
			node.fileContent=data;
			entities.put(getFullPath(dirPath,fileName),node);
			parentNode.child.put(fileName,node);
		}
		public void touchFile(String dirPath, String fileName){
			createFile(dirPath, fileName, "");
		}
		public String cat(String filePath){
			Entity node = entities.get(filePath);
			return node.fileContent;
		}

		private void replaceParentPath(Entity cur, 
			String oldPath, String newPath, boolean isRoot){
			for(Entity c: cur.child.values()){
				replaceParentPath(c, oldPath, newPath,false);
			}
			if(!isRoot){
				entities.remove(getFullPath(cur.parentPath,cur.name));
				cur.parentPath=cur.parentPath.replace(oldPath, newPath);
				entities.put(getFullPath(cur.parentPath,cur.name), cur);
			}
		}

		public void mv(String oldPath, String newPath){
			Pair<String, String> oldPaths = splitFullPath(oldPath);
			Entity oldParentNode=entities.get(oldPaths.getKey());
			Entity oldTargetNode=entities.get(oldPath);
			oldParentNode.child.remove(oldPaths.getValue());
			entities.remove(oldPath);
			Pair<String, String> newPaths = splitFullPath(newPath);
			Entity newParentNode=entities.get(newPaths.getKey());
			Entity newTargetNode=oldTargetNode;
			newTargetNode.name=newPaths.getValue();
			newTargetNode.parentPath=newPaths.getKey();
			newParentNode.child.put(newTargetNode.name, newTargetNode);
			entities.put(getFullPath(newTargetNode.parentPath,newTargetNode.name), newTargetNode);
			replaceParentPath(newTargetNode, oldPath,newPath, true);
		}

		public void rm(String path){
			Pair<String, String> paths = splitFullPath(path);
			Entity parentNode=entities.get(paths.getKey());
			Entity targetNode=entities.get(paths.getValue());
			parentNode.child.remove(paths.getValue());
			entities.remove(path);
		}

		public List<String> ls(String path){
			Entity parentNode=entities.get(path);
			return parentNode.child.values().stream()
			.map(e->getFullPath(e.parentPath,e.name))
			.collect(Collectors.toList());
		}
	}

	FileSystemServer server;

	@Before
	public void setUp(){
		server=new FileSystemServer();
	}

	@Test
	public void test_moveEntities_succeeds() {
		server.createDirectory("/rough/");
		server.touchFile("/rough/","a.cpp");
		server.createDirectory("/rough/dir1/");
		assertListSortedEquals(server.ls("/rough/")
			, new String[]{"/rough/a.cpp","/rough/dir1/"});
		server.mv("/rough/a.cpp","/rough/dir1/b.cpp");
		assertListSortedEquals(server.ls("/rough/")
			, new String[]{"/rough/dir1/"});
		assertListSortedEquals(server.ls("/rough/dir1/")
			, new String[]{"/rough/dir1/b.cpp"});
		server.mv("/rough/dir1/","/dir1/");
		assertTrue(server.ls("/rough/").isEmpty());
		assertListSortedEquals(server.ls("/")
			, new String[]{"/dir1/","/rough/"});
		assertListSortedEquals(server.ls("/dir1/")
			, new String[]{"/dir1/b.cpp"});
		server.touchFile("/dir1/","p1.py");
		server.createDirectory("/dir1/dir2/");
		server.touchFile("/dir1/dir2/","p2.py");
		server.touchFile("/dir1/dir2/","p3.py");
		assertListSortedEquals(server.ls("/dir1/")
			, new String[]{"/dir1/b.cpp","/dir1/dir2/","/dir1/p1.py"});
		assertListSortedEquals(server.ls("/dir1/dir2/")
			, new String[]{"/dir1/dir2/p2.py","/dir1/dir2/p3.py"});
		server.createDirectory("/rough/new1/");
		server.mv("/dir1/","/rough/new1/");
		assertListSortedEquals(server.ls("/rough/")
			, new String[]{"/rough/new1/"});
		assertListSortedEquals(server.ls("/rough/new1/")
			, new String[]{"/rough/new1/b.cpp","/rough/new1/dir2/","/rough/new1/p1.py"});
		assertListSortedEquals(server.ls("/rough/new1/dir2/")
			, new String[]{"/rough/new1/dir2/p2.py","/rough/new1/dir2/p3.py"});
	}

	@Test
	public void test_createFile_succeeds() {
		server.createDirectory("/cpp/");
		server.createFile("/cpp/","main.cpp","printf(\"hello!!!\");");
		assertListSortedEquals(server.ls("/"), new String[]{"/cpp/"});
		assertEquals(server.cat("/cpp/main.cpp"),"printf(\"hello!!!\");");
		server.touchFile("/cpp/", "sol.cpp");
		server.touchFile("/cpp/", "analysis.py");
		assertListSortedEquals(server.ls("/cpp/"), 
			new String[]{"/cpp/analysis.py","/cpp/main.cpp","/cpp/sol.cpp"});
	}

	@Test
	public void test_createDirectory_succeeds() {
		server.createDirectory("/a/");
		server.createDirectory("/b/");
		assertListSortedEquals(server.ls("/"), new String[]{"/a/","/b/"});
		server.createDirectory("/a/b/");server.createDirectory("/a/c/");
		assertListSortedEquals(server.ls("/a/"), new String[]{"/a/b/","/a/c/"});
		server.createDirectory("/a/b/c/");server.createDirectory("/a/b/d/");
		assertListSortedEquals(server.ls("/a/b/"), new String[]{"/a/b/c/","/a/b/d/"});
		server.createDirectory("/a/b/c/d/");
		assertListSortedEquals(server.ls("/a/b/c/"), new String[]{"/a/b/c/d/"});
	}

	private static void assertListSortedEquals(List<String>a, String[] b){
		assertArrayEquals(a.stream().sorted()
			.collect(Collectors.toList()).toArray(new String[0]), b);
	}

	public static void main(String []args){
		JUnitCore.main("InMemoryFileSystem");
	}
}