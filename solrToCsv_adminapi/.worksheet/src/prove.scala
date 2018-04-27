object prove {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(50); 
  
  
  val l = List("a", "b", "c");System.out.println("""l  : List[String] = """ + $show(l ));$skip(34); 
  


 val c = l.find(p => p=="c");System.out.println("""c  : Option[String] = """ + $show(c ));$skip(113); val res$0 = 
//an option value containing the first element in the list that satisfies p, or None if none exists.

c.nonEmpty;System.out.println("""res0: Boolean = """ + $show(res$0))}

//Seq("a", 1, 5L).collectFirst({ case x: Int => x*10 }) = Some(10)

//val v = c.exists(p)get
  
}
