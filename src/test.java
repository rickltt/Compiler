import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
/**
 * @author ltt
 * @date 2020/3/25 20:53
 */
public class test {
    static SymbolTable Table = new SymbolTable();
    public static void main(String[] args) throws IOException {
        //Syntax Cgramma = new Syntax();
        //LL1Table.preProcess(Cgramma);
        //LL1.dataRevision(Cgramma);
        //for(int i=0;i<Cgramma.size_of_prod;i++)
        //System.out.println(Cgramma.prod[i].left+"->"+Cgramma.prod[i].right);

        //Iterator iter1 =Cgramma.terminators.iterator();
        //while(iter1.hasNext()){
        //    System.out.println(iter1.next());
        //}

       // Iterator iter2=Cgramma.non_terminators.iterator();
       // while(iter2.hasNext()){
        //    System.out.println(iter2.next());
        //}
        lexAnalysis Lex = new lexAnalysis();
        Lex.LexAnaly();
        Table.getTable(Lex);
        Table.printtable();
        Syntax Cgramma = new Syntax();
        if (LL1Analysis.LL1_Analyse(Cgramma, Lex, Table)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
        SemanticAnalysis.PrintQt(Cgramma);
        ObjectCode.Blocked(Cgramma);


       // for (Syntax.Quaternary qt: Cgramma.ActiveLable
           //  ) {
            //System.out.println(qt.First+","+qt.Second+","+qt.Third+","+qt.Fourth);
        //}
        for (String[] str: Cgramma.code) {
            for(int i=0;i<str.length;i++){
                if(str[i]!=null)
                    System.out.print(str[i]);
            }

            System.out.println();
        }

        AsmCode asm =new AsmCode();
        asm.cToAsm(Cgramma,Table);
        for(int i=0;i<asm.preAsmCode.size();i++){
            System.out.println(asm.preAsmCode.get(i));
        }
        for(int i=0;i<asm.asmCode.size();i++){
            System.out.println(asm.asmCode.get(i));
        }
        //for(int j=0;j<asm.asmJump.length;j++){
          //  System.out.println(j+"  "+asm.asmJump[j]);
       // }
    }
}
