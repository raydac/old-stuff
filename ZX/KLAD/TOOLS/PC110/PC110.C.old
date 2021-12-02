/*************** micro-cd ***************/
/*					*/
/* Copyright (c) 1989 by Leon Obuhov    */
/* (based on Small-C R.Cain)            */
/*				25.11.89*/
/****************************************/

#define intwidth        2
#define charwidth	1

#define bspch	8
#define tabch	9
#define eolch	10
#define ffch	12
#define crch	13
#define quoch	39
#define bslch	92
#define eofil	26

#define nl	 outbyte(crch);outbyte(eolch)
#define tab	 outbyte(tabch)
#define colon	 outbyte(':')
#define comma	 outbyte(',')
#define space	 outbyte(' ')
#define comment	 outbyte(';')

#define symsiz		14
#define symtbsz		5040 /*(300+60)*14*/
#define numglbs		300
#define startglb	symtab
#define endglb		startglb+numglbs*symsiz
#define startloc	endglb+symsiz 
#define endloc		symtab+symtbsz-symsiz

#define name	0
#define ident	10
#define type	11
#define offset	12

#define namesize 10
#define namemax	 9

#define variable 1
#define array	 2
#define pointer	 3
#define function 4

#define cchar	1
#define cint	2

#define wqtabsz	100
#define wqsiz	4
#define wqmax	wq+wqtabsz-wqsiz

#define wqsym	0
#define wqloop	1
#define wqlab	2

#define litabsz	2500
#define litmax	litabsz-1

#define linesize 80
#define linemax	 linesize-1

#define macbsize 1500
#define mactsize 75
#define macssize 750

#define stif	 1
#define stwhile	 2
#define streturn 3
#define stbreak	 4
#define stcont	 5
#define stasm	 6
#define stexp	 7


char	symtab[symtbsz];
char	*glbptr,*locptr;
int     wq[wqtabsz];
int     *wqptr;

char	litq[litabsz];
int	litptr;

char	macb[macbsize];
char	mact[macssize];
char	*macbptr,*mactptr;
char	*macbmax;
int	macp[mactsize];
int	macpmax,*macpptr;

char	line[linesize];
char	mline[linesize];
char	*chr,*nexch,*chrmax,*mptr;
int	mpmax;


int	nxtlab,
	litlab,
	spt,
	argstk,
	ncmp,
	errcnt,
	inscnt,
	incnt,
	eof,
	ctext,
	toscreen,
	cmode,
	lastst;

char	quote[2],quoch1[2];
char	*cptr;
int     hasmain;

int    *input;
int    *input2;
int    *output;

int    *argv[4];
int    File_ds;

/**************** micCi *****************/
/*					*/
/*            main programm		*/
/*					*/
/*					*/
/****************************************/
main(argv)           /* body of compiler */
  int *argv[];
{ 
  setarg(argv);
  glbptr=startglb;
  locptr=startloc;
  chrmax=line+linemax;
  mpmax=mline+linemax;
  wqptr=wq;
  /**/   
  litptr=
  spt=
  eof=
  ncmp=
  lastst=
  0;
  /**/
  input=0;
  input2=0;
  output=0;
  /**/   
  errcnt=
  inscnt=
  incnt=
  hasmain=
  0;
  /**/   
  mactptr=mact;
  macbptr=macb;
  macpptr=macp;
  macpmax=macp+(mactsize-1)*intwidth;
  macbmax=macb+macbsize-1;
  /**/   
  quote[0]='"'; quoch1[0]=quoch;
  quote[1]=0;   quoch1[1]=0;
  cmode=1; toscreen=1;
  /**/  
  ask();
  openout(argv);
  openin (argv);
  outstr(";************** micro-cd ****************");
  nl;
  outstr(";*					*");
  nl;
  outstr(";*  Copyright (c) 1989 by Leon Obuhov   *");
  nl;
  outstr(";*  (based on Small-C R.Cain)           *");
  nl;
  outstr(";*					*");
  nl;
  outstr(";****************************************");
  nl;
  parse();
  dumplits();
  dumpglbs();
  trailer();
  closeout();
  errorsummary();
  exit();return;
}  
/*********** syntax analyzer ************/
parse()		/* syntax analyzer */
{        
  while(eof==0)
  { 
    if (amatch("char",4))
    { declglb (cchar); needsemi();}
    else if (amatch ("int",3))
    { declglb(cint);   needsemi();}
    else if (match  ("#asm"))
      doasm();
    else if (match ("#include"))
      openinclude();
    else if (match ("#define"))
      addmac();
    else newfunc();
      skipblanks();
  }  
}   
newfunc()	/* declare function */
{         
  char n[namesize],*ptr;
  int argtop;
  /**/
  if (symname(n)==0)
  {  
    error ("illegal function or declaration");
    resetptr();
    return;
  }  
  if (ptr=findglb(n))
  {  
    if (ptr[ident]!=function) errmulti(n);
    else if (ptr[offset]==function) errmulti(n);
    else ptr[offset]=function;
  }  
  else addglb (n,function,cint,function);
  /**/  
  if (match ("(")==0)
    error ("missing opening parenthesis");
  if (astreq (n+1,"main",4))
  { 
     hasmain=1; header();
  } 
  outstr ("_"); outstr(n); colon; 
  nl;
  pushbpsp();
  /**/  
  locptr=startloc;
  argstk=0;
  while (match(")")==0)
  {  
    if (symname(n))
    {  
      if (findloc(n)) errmulti(n);
      else
      {  
        addloc(n,0,0,argstk);
        argstk=argstk+intwidth;
      }  
    }  
      else { error("Illegal argument name"); skipchars();}
      skipblanks();
      if (streq (chr,")")==0)
      {  
        if (match (",")==0) error ("expected comma");
      }  
      if (needstend()) break;
  }  
  spt=0; argtop=argstk;
  while (argstk)
  {  
    if (amatch ("char",4))
    { getarg (cchar,argtop); needsemi();}
    else if (amatch ("int",3))
    { getarg (cint,argtop); needsemi();}
    else {error ("wrong number o f args"); break;}
  }  
  if (statement()!=streturn)
  {  
    popbpsp();
    ret();
  }  
  spt=0;
  locptr=startloc;
}   
getarg (t,argtop) /* arg of declare function */
  int t,argtop;
{         
  int j,legalname,address;
  char n[namesize],c,*argptr;
  /**/   
  while (1)
  {  
    if (argstk==0) return;
    if (match ("*")) j=pointer; else j=variable;
    if ((legalname=symname(n))==0) errname();
    if (match ("["))
    {  
      while (inbyte()!=']')
        if (needstend()) break;
      j=pointer;
    }  
    if (legalname)
    {  
      if (argptr=findloc(n))
      {  
        argptr[ident]=j;
        argptr[type]=t;
          address=argtop-((argptr[offset]&255) +
            ((argptr[offset+1]&255)<<8)) + 2;
        argptr[offset]=address;
            argptr[offset+1]=address>>8;
      }  
    } 		else error("expected argument name");
    argstk=argstk-intwidth;
    if (needstend()) return;
    if (match(",")==0) error ("expected comma");
  }  
}   
statement()	/* operator */
{         
  if ((*chr==0) & (eof)) return;
  else if (amatch ("char",4))
  { 
    declloc (cchar); needsemi();
  } 
  else if (amatch ("int",3))
  { 
    declloc (cint ); needsemi();
  } 
  else if (match ("{"))
    compound();
  else if (amatch ("if",2))
  { 
    doif(); lastst=stif;
  } 
  else if (amatch ("while",5))
  { 
    dowhile(); lastst=stwhile;
  } 
  else if (amatch ("return",6))
  { 
    doreturn(); needsemi(); lastst=streturn;
  } 
  else if (amatch ("break",5))
  { 
    dobreak(); needsemi(); lastst=stbreak;
  } 
  else if (amatch ("continue",8))
  { 
    docont(); needsemi(); lastst=stcont;
  } 
  else if (match (";"));
  else if (match ("#asm"))
  { 
    doasm(); lastst=stasm;
  } 
  else{ expression(); needsemi(); lastst=stexp;}
  return lastst;
}   
compound()	/* group of operators */
{         
  ++ncmp;
  while (match ("}")==0)
    if (eof) return; else statement();
  --ncmp;
}   
doif()		/* if ... else */
{         
  int flev,fsp,flab1,flab2;
  /**/   
  flev=locptr;
  flab1=getlabel();
  test(flab1);
  statement();
  locptr=flev;
  if (amatch ("else",4)==0)
  {  
    outlabel (flab1); colon; nl;
    return;
  }  
  jump (flab2=getlabel());
  outlabel(flab1); colon; nl;
  statement();
  locptr=flev;
  outlabel (flab2); colon; nl;
}   
dowhile()	/* while ...   */
{         
  int wq[4];
  /**/   
  wq[wqsym]=locptr;
  wq[wqloop]=getlabel();
  wq[wqlab]=getlabel();
  addwhile (wq);
  outlabel (wq[wqloop]); colon; nl;
  test (wq[wqlab]);
  statement();
  jump (wq[wqloop]);
  outlabel (wq[wqlab]); colon; nl;
  locptr=wq[wqsym];
  delwhile();
}   
doreturn()	/* return (...)*/
{         
  if (needstend()==0) expression();
  popbpsp();
  ret();
}   
dobreak()	/* break;  */
{         
  int *ptr;
  /**/ 
  if ((ptr=readwhile())==0) return;
  jump (ptr[wqlab]);
}   
docont()	/* continue; */
{         
  int *ptr;
  /**/
  if ((ptr=readwhile())==0) return;
  jump ((ptr[wqloop]));
}   
doasm()		/* #asm ... #endasm */
{         
  cmode=0;
  while(1)
  {  
    inline();
    if (match ("#endasm")) break;
    if (eof) break;
    outstr (line);
    nl;
  } 
  outstr (";  #endasm");nl;
  resetptr();
  cmode=1;
}   
expression()	
{         
  int lval[2];
  /**/ 
  if (hier1 (lval)) rvalue (lval);
}   
/**************** micCi *****************/
/*					*/
/*        analyze of expression		*/
/*					*/
/*					*/
/****************************************/
hier1 (lval)	/* hier2 = hier1  */
  int lval[];
{         
  int k,lval2[2];
  /**/ 
  k=hier2(lval);
  if (match ("="))
  {  
    if (k==0) {errlval(); return 0;}
    if (lval[1]) push();
    if (hier1 (lval2)) rvalue (lval2);
    if (lval[1]) pop();
    store (lval);
    return 0;
  }  
  else return k;
}   
hier2 (lval)	/* hier3 | hier3  */
  int lval[];
{         
  int k,lval2[2];
  /**/ 
  k=hier3(lval);
  skipblanks();
  if (*chr!='|') return k;
  if (k) rvalue (lval);
  while(1)
  {  
    if (match ("|"))
    {  
      push();
      if (hier3(lval2)) rvalue (lval2);
      pop();
      or();		/*  or	*/
    }  
    else return 0;
  }  
}   
hier3 (lval)	/* hier4 ^ hier4  */
  int lval[];
{         
  int k,lval2[2];
  /**/ 
  k=hier4(lval);
  skipblanks();
  if (*chr!='^') return k;
  if (k) rvalue (lval);
  while(1)
  {  
    if (match ("^"))
    {  
      push();
      if (hier4(lval2)) rvalue (lval2);
      pop();
      xor();		/* xor	*/
    }  
    else return 0;
  }  
}   
hier4 (lval)	/* hier5 & hier5  */	
  int lval[];
{         
  int k,lval2[2];
  /**/ 
  k=hier5(lval);
  skipblanks();
  if (*chr!='&') return k;
  if (k) rvalue (lval);
  while(1)
  {  
    if (match ("&"))
    {  
      push();
      if (hier5(lval2)) rvalue (lval2);
      pop();
      and();		/*  and	*/
    }  
    else return 0;
  }  
}   
hier5 (lval)    /* hier6 == hier6, != hier6 */
  int lval[];
{         
  int k,lval2[2];
  /**/ 
  k=hier6(lval);
  skipblanks();
  if (*nexch!='=') return k;
  if ((*chr!='=') & (*chr!='!')) return k;
  if (k) rvalue (lval);
  while(1)
  {  
    if (match ("=="))
    {  
      push();
      if (hier6(lval2)) rvalue (lval2);
      pop();
      tst("==");		/*  eq	*/
    }  
    else if (match ("!="))
    {  
      push();
      if (hier6 (lval2)) rvalue (lval2);
      pop();
      tst("!=");		/* ne	*/
    }  
    else return 0;
  }  
}   
hier6 (lval)    /* hier7 <= hier7, >= hier7, < hier7, >hier7 */
  int lval[];
{         
  int k,lval2[2];
  /**/ 
  k=hier7(lval);
  skipblanks();
  if ((*chr!='<') & (*chr!='>')) return k;
  if ((*nexch=='<') | (*nexch=='>')) return k;
  if (k) rvalue (lval);
  while(1)
  {  
    if (match ("<="))
    {  
      push();
      if (hier7(lval2)) rvalue (lval2);
      pop();
      if (cptr=lval[0])
        if (cptr[ident]==pointer)
        {  
          tst("<=.");		/* ule 	*/
          continue;
        }  
      if (cptr=lval2[0])
        if (cptr[ident]==pointer)
        {  
          tst("<=.");		/* ule 	*/
          continue;
        }  
      tst("<=");			/* le	*/
    }  
    else if (match (">="))
    {  
      push();
      if (hier7(lval2)) rvalue (lval2);
      pop();
      if (cptr=lval[0])
        if (cptr[ident]==pointer)
        {  
          tst(">=.");		/* uge 	*/
          continue;
        }  
      if (cptr=lval2[0])
        if (cptr[ident]==pointer)
        {  
          tst(">=.");		/* uge 	*/
          continue;
        }  
      tst(">=");			/* ge	*/
    }  
    else if ((streq (chr,"<")) & (streq (chr,"<<")==0))
    {  
      inbyte();
      push();
      if (hier7(lval2)) rvalue (lval2);
      pop();
      if (cptr=lval[0])
        if (cptr[ident]==pointer)
        {  
          tst("<.");		/* ult 	*/
          continue;
        }  
      if (cptr=lval2[0])
        if (cptr[ident]==pointer)
        {  
          tst("<.");		/* ult 	*/
          continue;
        }  
      tst("<");			/* lt	*/
    }  
    else if ((streq (chr,">")) & (streq (chr,">>")==0))
    {  
      inbyte();
      push();
      if (hier7(lval2)) rvalue (lval2);
      pop();
      if (cptr=lval[0])
        if (cptr[ident]==pointer)
        {  
          tst(">.");		/* ugt 	*/
          continue;
        }  
      if (cptr=lval2[0])
        if (cptr[ident]==pointer)
        {  
          tst(">.");		/* ugt 	*/
          continue;
        }  
      tst(">");			/* gt	*/
    }  
    else return 0;
  }  
}   
hier7 (lval)    /* hier8 >> hier8, <<hier8  */
  int lval[];
{         
  int k,lval2[2]; 
  /**/ 
  k=hier8(lval);
  skipblanks();
  if ((streq (chr,">>")==0) & (streq (chr,"<<")==0)) return k;
  if (k) rvalue (lval);
  while(1)
  {  
    if (match (">>"))
    {  
      push();
      if (hier8(lval2)) rvalue (lval2);
      pop();
      asr();			/* asr	*/
    }  
    else if (match ("<<"))
    {  
      push();
      if (hier8(lval2)) rvalue (lval2);
      pop();
      asl();			/* asl	*/
    }  
    else return 0;
  }  
}  
hier8(lval)     /* hier9 + hier9 , - hier9 */
  int lval[];
{         
  int k,lval2[2];
  /**/ 
  k=hier9(lval);
  skipblanks();
  if ((*chr!='+') & (*chr!='-')) return k;
  if (k) rvalue (lval);
  while(1)
  {  
    if (match ("+"))
    {  
      push();
      if (hier9(lval2)) rvalue (lval2);
      if (cptr=lval[0])
        if ((cptr[ident]==pointer) &
           (cptr[type]==cint)) scale();
      pop();
      add();			/* add	*/
    }  
    else if (match ("-"))
    {  
      push();
      if (hier9(lval2)) rvalue (lval2);
      if (cptr=lval[0])
        if ((cptr[ident]==pointer) &
           (cptr[type]==cint)) scale();
      pop();
      sub();			/* sub	*/
    }  
    else return 0;
  }  
}   
hier9(lval)     /* hier10*hier10 , / hier10, % hier10 */
  int lval[];
{         
  int k,lval2[2];
  /**/ 
  k=hier10(lval);
  skipblanks();
  if ((*chr!='*') & (*chr!='/') & (*chr!='%')) return k;
  if (k) rvalue (lval);
  while(1)
  {  
    if (match ("*"))
    {  
      push();
      if (hier9(lval2)) rvalue (lval2);
      pop();
      mult();			/* mult */
    }  
    else if (match ("/"))
    {  
      push();
      if (hier10(lval2)) rvalue (lval2);
      pop();
      div();			/* div	*/
    } 	
    else if (match ("%"))
    {  
      push();
      if (hier10(lval2)) rvalue (lval2);
      pop();
      mod();			/* mod	*/
    }  
    else return 0;
  }  
}   
hier10(lval)    /* ++,--,-,*,& hier10, hier11++, hier11-- */
  int lval[];
{         
  int k;
  char *ptr;
  /**/ 
  if (match ("++"))
  {  
    if ((k=hier10(lval))==0) { errlval(); return 0;}
    if (lval[1]) push();
    rvalue (lval);
    ptr=lval[0];
    if((ptr[ident]==pointer)&(ptr[type]==cint))
      inc (intwidth);		/* inc */
    else inc (charwidth);
    if (lval[1]) pop();
    store (lval);
    return 0;
  }  
  else if (match ("--"))
  {  
    if ((k=hier10(lval))==0) { errlval(); return 0;}
    if (lval[1]) push();
    rvalue (lval);
    ptr=lval[0];
    if((ptr[ident]==pointer)&(ptr[type]==cint))
      dec (intwidth);		/* dec	*/
    else dec (charwidth);
    if (lval[1]) pop();
    store (lval);
    return 0;
  }  
  else if (match ("-"))
  {  
    k=hier10(lval);
    if (k) rvalue (lval);
    neg();			/* neg	*/
    return 0;
  }  
  else if (match ("*"))
  {  
    k=hier10(lval);
    if (k) rvalue (lval);
    lval[1]=cint;
    if (ptr=lval[0]) lval[1]=ptr[type];
    lval[0]=0;			/* ptr	*/
    return 1;
  }  
  else if (match ("&"))
  {  
    k=hier10(lval);
    if (k==0) { error("illegal address"); return 0; }
    else if (lval[1]) return 0;
      else
    {  
      immedl();			/*immed */
      outstr (ptr=lval[0]);
      nl;
      lval[1]=ptr[type];
    return 0;
    }  
  }  
  else
  {  
    k=hier11 (lval);
    if (match ("++"))
    {  
      if (k==0) { errlval(); return 0; }
      if (lval[1]) push();
      rvalue (lval);
      ptr=lval[0];
      if((ptr[ident]==pointer)&(ptr[type]==cint))
        inc (intwidth);
      else inc (charwidth);
      if (lval[1]) pop();
      store (lval);
      if((ptr[ident]==pointer)&(ptr[type]==cint))
        dec (intwidth);
      else dec (charwidth);
      return 0;
    }  
    else if (match ("--"))
    {  
      if (k==0) { errlval(); return 0; }
      if (lval[1]) push();
      rvalue (lval);
      ptr=lval[0];
      if((ptr[ident]==pointer)&(ptr[type]==cint))
        dec (intwidth);
      else dec (charwidth);
      if (lval[1]) pop();
      store (lval);
      if((ptr[ident]==pointer)&(ptr[type]==cint))
        inc (intwidth);
      else inc (charwidth);
      return 0;
    }  
    else return k;
  }  
}   
hier11(lval)    /* primary[...], primary(...), primary */
  int *lval;
{         
  int k;
  char *ptr;
  /**/ 
  k=primary(lval);
  ptr=lval[0];
  skipblanks();
  if ((*chr=='[') | (*chr=='('))
    while(1)
    {  
      if (match ("["))
      {  
        if (ptr==0)
        {  
          error ("cannot subscript");
          skipchars();
          needbrack ("]");
          return 0;
        }  
        else if ( ptr[ident]==pointer)
          rvalue(lval);
        else if ( ptr[ident]!=array)
        {  
          error ("cannot subscript");
          k=0;
        }  
        push();
        expression();
        needbrack("]");
        if (ptr[type]==cint) scale();
        pop();
        add();
        lval[0]=0;
        lval[1]=ptr[type];
        k=1;
      }  
      else if (match ("("))
      {  
        if (ptr==0) callfunction(0);
        else if (ptr[ident]!=function)
        { 
          rvalue (lval);
          callfunction(0);
        }  
        else callfunction(ptr);
        k=lval[0]=0;
      }  
      else return k;
    }  		/* end of while */
  if (ptr==0) return k;
  if (ptr[ident]==function)
  {  
    immedl();
    outstr(ptr);
    nl;
    return 0;
  }  
  return k;
}   
primary(lval)   /* (hier1), symname_loc, symname_glb, constant */
  int *lval;
{         
  char *ptr,sname[namesize];
  int num[1];
  int k;
  /**/ 
  if (match ("("))
  {  
    k=hier1(lval);
    needbrack (")");
    return k;
  }  
  if (symname(sname))
  {  
    if (ptr=findloc(sname))
    {  
      getloc (ptr);
      lval[0]=ptr;
      lval[1]=ptr[type];
      if (ptr[ident]==pointer) lval[1]=cint;
      if (ptr[ident]==array) return 0; else return 1;
    }  
    if (ptr=findglb(sname))
    { 
      if (ptr[ident]!=function)
      {  
        lval[0]=ptr;
        lval[1]=0;
        if (ptr[ident]!=array) return 1;
        immedl();
        outstr (ptr);
        nl;
        lval[1]=ptr[type];
        return 0;
      }  
    }  
    ptr=addglb (sname,function,cint,0);
    lval[0]=ptr;
    lval[1]=0;
    return 0;
  }  
  if (constant (num)) return (lval[0]=lval[1]=0);
  else
  {  
    error ("invalid expression");
    immed();
    outdec(0);
    nl;
    skipchars();
    return 0;
  }  
}   
/**************** micCi *****************/
/*					*/
/* package for tables in SYMTAB		*/
/* add, find and delete			*/
/*					*/
/****************************************/
callfunction(ptr)	/* func(...); */
  char *ptr;
{         
  int nargs;
  /**/
  nargs=0;
  skipblanks();
  if (ptr==0) push(); 
  while (*chr!=')')
  {  
    if (needstend()) break;
    expression();
    push();
    nargs=nargs+intwidth;
    if (match (",")==0) break;
  }  
  needbrack(")");
  if (ptr) call (ptr+1);
  else callstk();
  spt=modstk(spt+nargs);
}   
declglb(typ)	/* declare global var */
  int typ;
{         
  int k,j; char sname[namesize];
  /**/
  while(1)
  {  
    while(1)
    {  
      if (needstend()) return;
      k=1;
      if (match("*")) j=pointer;
      else j=variable;
      if (symname(sname)==0) errname();
      if (findglb(sname)) errmulti (sname);
      if (match ("["))
      {  
        k=needsub();
        if (k) j=array;
        else j=pointer;
      }  
      addglb (sname,j,typ,k);
      break;
    }  
    if (match (",")==0) return;
  }  
}   
declloc(typ)	/* declare local var  */
  int typ;
{         
  int k,j,k1; char sname[namesize];
  /**/
  k1=0;
  while(1)
  {  
    while(1)
    {  
      if (needstend())
      { 
        modstk (spt-k1); return;
      } 
      if (match("*")) j=pointer;
      else j=variable;
      if (symname(sname)==0) errname();
      if (findloc(sname)) errmulti (sname);
      if (match ("["))
      {  
        k=needsub();
        if (k)
        {  
          j=array;
          k=k*intwidth;
        }  
        else
        {  
          j=pointer;
          k=intwidth;
        }  
      }  
      else k=intwidth;
      k1=k1+k;
      spt=spt-k;
      addloc (sname,j,typ,spt);
      break;
    }  
    if (match (",")==0)
    { 
      modstk (spt-k1); return;
    } 
  }  
}   
addglb (sname,id,typ,value) /* to symtab */
  char *sname,id,typ;
  int value;
{         
  char *ptr;
  /**/
  if (cptr=findglb (sname)) return cptr;
  if (glbptr>=endglb)
  {  
    error ( "globals symbol table overflow");
    return 0;
  }  
  cptr=ptr=glbptr;
  while (*ptr++=*sname++);
  cptr[ident]=id;
  cptr[type]=typ;
  cptr[offset]=value;
  cptr[offset+1]=value>>8;
  glbptr=glbptr+symsiz;
  return cptr;
}   
findglb (sname)		/* find in symtab */
  char *sname;
{         
  char *ptr;
  /**/
  ptr=startglb;
  while (ptr!=glbptr)
  {  
    if ( *sname==*ptr)
      if (streq(sname,ptr)) return ptr;
    ptr=ptr+symsiz;
  }  
  return 0;
}   
addloc (sname,id,typ,value)	/* to symtab */
  char *sname,id,typ;
  int value;
{         
  char *ptr;
  /**/
  if (cptr=findloc (sname)) return cptr;
  if (locptr>=endloc)
  {  
    error ( "local symbol table overflow");
    return 0;
  }  
  cptr=ptr=locptr;
  while (*ptr++=*sname++);
  cptr[ident]=id;
  cptr[type]=typ;
  cptr[offset]=value;
  cptr[offset+1]=value>>8;
  locptr=locptr+symsiz;
  return cptr;
}   
findloc (sname)		/* find in symtab */
  char *sname;
{         
  char *ptr;
  /**/
  ptr=startloc;
  while (ptr!=locptr)
  {  
    if ( *sname==*ptr)
      if (streq(sname,ptr)) return ptr;
    ptr=ptr+symsiz;
  }  
  return 0;
}   
addmac()		/* to macb,mact */
{         
  char sname[namesize],*sn,*mn;
  /**/
  if (symname (sname)==0)
  {  
    errname();
    resetptr();
    return;
  }  
  sn=sname; mn=mactptr;
  if (macpmax>=macpptr)
  {  
    while (*mn++=*sn++);
    *macpptr++=macbptr; mactptr=mactptr+namesize;
  }  
  else error ("macro count exceeded");
  while (*chr==' ' | *chr==tabch) gch();
  while (*macbptr++=gch())
  {  
    if (macbptr>=macbmax)
    {  error ("macro table full"); break; }
  }  
}   
findmac (sname)		/* find in mact */
  char *sname;
{         
  int *mqp;
  char *mqt;
  /**/
  mqp=macp; mqt=mact;
  while (mqp<macpptr)
  {  
    if ( *sname==*mqt)
      if (streq(sname,mqt)) return (*mqp);
    mqt=mqt+namesize; mqp++;
  }  
  return 0;
}   
addwhile (ptr)	/* push to stack for while */
  int ptr[];
{         
  int k;
  /**/
  if (wqptr==wqmax)
  { 
    error ("too many active whiles"); return;
  } 
  k=0;
  while (k<wqsiz)
  { 
    *wqptr++=ptr[k++];
  } 
}   
delwhile()	/* pop from stack of while */
{         
  if (readwhile()) wqptr=wqptr-wqsiz;
}  
readwhile()	/* read from stack while */
{         
  if (wqptr==wq)
  { 
    error ("no active while"); return 0;
  } 
  else return (wqptr-wqsiz);
}   
getlabel()	/* current number for lab */
{         
  return (++nxtlab);
}   
/**************** micCi *****************/
/*					*/
/*	   package for INPUT		*/
/*					*/
/*					*/
/****************************************/
symname (sname) /* in sname ptr to leksem */
  char *sname;
{         
  int k;
  skipblanks();
  if (alpha (*chr)==0) return 0;
  k=1;
  while (an (*chr)) sname[k++]=gch();
  sname[k]=0;
  sname[0]=k-1;
  return 1;
}   
number (val)	/* get number in val */
  int val[];
{         
  int k,minus;
  char c;
  /**/
  k=minus=1;
  while (k)
  {  
    k=0;
    if (match ("+")) k=1;    
    if (match ("-")) { minus=(-minus); k=1; }
  }  
  if (numeric (*chr)==0) return 0;
  while (numeric (*chr))
  {  
    c=inbyte();
    k=k*10+(c-'0');
  }  
  if (minus<0) k=(-k);
  val[0]=k;
  return 1;
}   
constant (val)	/* get number,char or string on val */
  int val[];
{         
  if (number (val)) immed();
  else if (getqchar (val)) immed();
  else if (getqstring (val))
  {  
    immedl();
    outlabel (litlab);
    outbyte ('.');
  }  
  else return 0;
  outdec (val[0]);
  nl;
  return 1;
}   
getqchar (val)	/* get char in val */
  int val[];
{         
  int k;
  char c;
  k=0;
  if (match (quoch1)==0) return 0;
  if ((c=gch())==bslch)
  {  
    if ((c=gch())=='n') k=eolch;
    else if (c=='t') k=tabch;
    else if (c=='b') k=bspch;
    else if (c=='r') k=crch;
    else if (c=='f') k=ffch;
    else if (c=='\\')k=bslch;
    else if (c=='0') k=0;
    else k=c;
  }  
  else k=c;
  if (match (quoch1)==0) return 0;
  val[0]=k;
  return 1;
}   
getqstring (val)/* get string in val */
  int val[];
{         
  char c;
  /**/
  if (match (quote)==0) return 0;
  val[0]=litptr;
  while (*chr!='"')
  {  
    if (*chr==0) break;
    if (litptr>=litmax)
    {  
      error ("string space exhausted");
      while (match (quote)==0) if (gch()==0) break;
      return 1;
    }  
    litq[litptr++]=gch();
  }  
  gch();
  litq[litptr++]=0;
  return 1;
}   
streq (str1,str2) /* equal ? */
  char *str1,*str2;
{         
  int k;
  /**/
  k=0;
  while (*str2)
  {  
    if ((*str1)!=(*str2)) return 0;
    k++; str1++; str2++;
  }  
  return k;
}   
astreq (str1,str2,len) /* streq according leng */
  char *str1,*str2;
  int len;
{         
  int k;
  /**/
  k=0;
  while (k<len)
  {  
    if ((*str1)!=(*str2)) break;
    if (*str1==0) break;
    if (*str2==0) break;
    k++; str1++; str2++;
  }  
  if (an (*str1)) return 0;
  if (an (*str2)) return 0;
  return k;
}   
match (lit)	/* eq ? with incr *chr if equal */
  char *lit;
{         
  int k;
  /**/
  skipblanks();
  if (k=streq (chr,lit))
  { 
    chr=chr+k; nexch=chr+1; return 1;
  } 
  return 0;
}   
amatch (lit,len) /* match according leng */
  char *lit;
  int len;
{         
  int k;
  /**/
  skipblanks();
  if (k=astreq (chr,lit,len))
  {  
    chr=chr+k; nexch=chr+1;
    while (an (*chr)) inbyte();
    return 1;
  }  
  return 0;
}   
needsub()	/* get dimension of array */
{         
  int num[1];
  /**/
  if (match ("]")) return 0;
  if (number (num)==0)
  {  
    error ("must be constant");
    num[0]=1;
  }  
  if (num[0]<0)
  {  
    error ("negative size illegal");
    num[0]=(-num[0]);
  }  
  needbrack ("]");
  return num[0];
}   
needsemi()
{         
  if (match (";")==0) error ("missing semicolon");
}   
needstend()
{         
  skipblanks();
  return ((streq (chr,";") | (*chr==0)));
}   
needbrack (str)
  char *str;
{         
  if (match (str)==0)
  { 
    error ("missing bracket");
    comment; outstr(str); nl;
  }  
}   
skipblanks()
{         
  while(1)
  {  
    while(*chr==0)
    {  
      inline();
      preprocess();
      if (eof) break;
    }  
    if (*chr==' ') gch();
    else if (*chr==tabch) gch(); else return;
  }  
}   
skipchars()
{         
  if (an (inbyte())) while (an (*chr)) gch();
  else while (an (*chr)==0)
  { 
    if (*chr==0) break; gch();
  } 
  skipblanks();
}   
alpha (c)	/* [a-z,A-Z] */
  char c;
{         
  if ((c>='a') & (c<='z')) return 1;
  if ((c>='A') & (c<='Z')) return 1;
  return (c=='_');
}   
numeric (c)	/* [0-9] */
  char c;
{         
  return ((c>='0') & (c<='9'));
}   
an (c)		/* [a-z,A-Z,0-9] */
  char c;
{         
  if ((c>='0') & (c<='9')) return 1;
  if ((c>='a') & (c<='z')) return 1;
  if ((c>='A') & (c<='Z')) return 1;
  return (c=='_');
}   
inbyte()	/* get stream byte with preprocess */
{         
  while (*chr==0)
  {  
    if (eof) return 0;
    inline();
    preprocess();
  }  
  return gch();
}   
inchar()	/* - " - without preprocess */
{         
  if (*chr==0) inline();
  if (eof) return 0;
  return (gch());
}   
inline()	/* lowlevel input 80-line */
{         
  int k; int *unit;
  /**/
  while(1)
  {  
    if (input==0) eof=1;        /* input*/
    if (eof) return;
    if ((unit=input2)==0) unit=input;
    resetptr();
    while ((k=fgetc(unit))!=eofil)   /* unit */
    { 
      if (feof(unit)!=0) break;
      if ((k==eolch) | (chr >= chrmax))
      {  incnt++; break; }
      *chr++=k;
    }  
    *chr=0;
    if ( k==eofil | feof(unit) )
    {  
      fclose (unit);
      if (input2) input2=0; else input=0;
    }  
    if (chr > line)
    {  
      if ((ctext) & (cmode))
      {  
        comment;
        outstr (line);
        nl;
      }  
      chr=line; nexch=chr+1;
      return;
    }  
  }  
}   
preprocess()
{         
  int k;
  char c,sname[namesize],*mq;
  /**/
  if (cmode==0) return;
  mptr=mline; chr=line; nexch=chr+1;
  while (*chr)
  {  
    if ((*chr==' ') | (*chr==tabch))
    {  
      keepchr (' ');
      while ((*chr==' ') | (*chr==tabch)) gch();
    }  
    else if (*chr=='"')
    {  
      keepchr (*chr);
      gch();
      while (*chr!='"')
      {  
        if (*chr==0)
        {  error ("missing quote"); break; }
        keepchr (gch());
      }  
      gch();
      keepchr ('"');
    }  
    else if (*chr==quoch)
    {  
      keepchr (quoch);
      gch();
      while (*chr!=quoch)
      {  
        if (*chr==0)
        {  error ("missing apostrof"); break; }
        keepchr (gch());
      }  
      gch();
      keepchr (quoch);
    }  
      else if ((*chr=='/') & (*nexch=='*'))
      {  
        inchar();
        inchar();
        while (((*chr=='*') & (*nexch=='/'))==0)
        {  
          if (*chr==0) inline(); else inchar();
          if (eof) break;
        }  
        inchar();
        inchar();
      }  
      else if (an (*chr))
      {  
        k=1;
        while (an (*chr))
        {  
          if (k<namemax) sname[k++]=*chr;
          gch();
        }  
        sname[k]=0; sname[0]=k-1;
        if (mq=findmac (sname))
          while (c=*mq++) keepchr (c);
        else
        {  
          k=1;
          while (c=sname[k++]) keepchr (c);
        }  
      }  
      else keepchr (gch());
  }  
  keepchr (0);
  if (mptr>=mpmax) error ("line too long");
  mptr=mline; chr=line; nexch=chr+1;
  while (*chr++=*mptr++);
  chr=line;
}   
resetptr()	/* clear buf line, chr=line */
{         
  chr=line; nexch=chr+1; *chr=0;
}   
gch()		/* get byte from buf line, ++chr */
{         
  if (*chr==0) return 0;
  else { nexch++; return (*chr++); }
}   
keepchr (c)	/* for preprocess */
  char c;
{         
  *mptr=c;
  if (mptr<mpmax) mptr++;
  return c;
}   
/**************** micCi *****************/
/*					*/
/*	  opening and closing		*/
/*					*/
/*					*/
/****************************************/
ask()		/* initial interface with user */
{         
  resetptr();
  display ("     **** micCi Umbrella company **** ");
  display ("     **Copyright (c) 1989 Obuhov L.** ");
  nl;
  nxtlab=1;
  litlab=getlabel();
  display ("     need C-text ?");
  ctext = reply();
  display ("     need to screen ?");
  toscreen = reply();
  resetptr();
}   
display (str)	/* message to user */
  char *str;
{         
  int k;
  /**/
  k=0;
  putchar (crch);
  putchar (eolch);
  while (str[k]) putchar (str[k++]);
}   
reply()
{         
  char l;
  resetptr();
  l=getchar();
  if ((l=='Y') | (l=='y')) return 1; else return 0;
}   
openout(argv)
  int *argv[];
{         
  resetptr();
  output=0;
  if ((output=fopen (argv[2],1))==0)
  { 
    output=0; error ("Open output failer");
  } 
  resetptr();
}   
openin (argv)
  int *argv[];
{         
  input=0;
  while (input==0)
  {  
    resetptr();
    if (eof) break;
    if ((input=fopen (argv[1],0))==0)         /* line */
    { 
      input=0; error ("Open input failer");
    } 
  } 
  resetptr();
}   
openinclude()
{         
  skipblanks();
  if ((input2=fopen (chr,0))==0)
  { 
  input2=0; error ("Open failer on include file");
  } 
  resetptr();
}   
closeout()
{         
  if (output) fclose (output);
  output=0;
}   
/**************** micCi *****************/
/*					*/
/*		E R R O R		*/
/*					*/
/*					*/
/****************************************/
errlval()
{         
  error ("must be left value");
}   
errmulti (sname)
  char *sname;
{         
  error ("already defined");
  comment;
  outstr (sname); nl;
}   
errname()
{         
  error ("illegal symbol name"); skipchars();
}   
error (ptr)
  char ptr[];
{         
  char *k;
  /**/
  comment; outstr (line); nl; comment;
  k=line;
  while (k<chr)
  {  
    if (*k==tabch) tab; else space;
    ++k;
  }  
  outbyte ('^');
  nl; comment; outstr ("______");
  outstr (ptr);
  outstr ("______");
  display (line);
  display (ptr); nl;
  ++errcnt;
}   
errorsummary()
{         
  if (ncmp) error ("missing closing bracket"); 
  nl;
  outdec (errcnt);
  outstr (" errors in compilation."); 
  nl;
  outdec (inscnt);
  outstr (" instructions generated."); 
  nl;
  outdec (incnt);
  outstr (" source lines."); 
  nl;
}   
/**************** micCi *****************/
/*					*/
/*	    package for OUTPUT		*/
/*					*/
/*					*/
/****************************************/
outbyte (c)
  char c;
{         
  if (c==0) return 0;
  if (output)
  { 
    if (toscreen)
    { 
      putchar (c);return c;
    } 
    else fputc (c,output);
    
  }  
  else putchar (c);
  return c;
}   
outdec (num)
  int num;
{         
  int k,zs;
  char c;
  /**/
  zs=0;
  k=10000;
  if (num<0)
  {  
    num = (-num);
    outbyte ('-');
  }  
  while (k>=1)
  {  
    c=num/k+'0';
    if ((c!='0') | (k==1) | (zs)) { zs=1; outbyte (c); }
    num = num % k;
    k = k/10;
  }  
}   
outstr (ptr)
  char ptr[];
{         
  int k;
  /**/
  if (ptr[0]<32) k=1; else k=0;
  while (outbyte (ptr[k++]));
}   
outlabel (label)
  int label;
{         
  outstr ("cc");
  outdec (label);
}   
outline (ptr)
  char ptr[];
{ 
  outtab (ptr); nl;
} 
outtab (ptr)
  char ptr[];
{ 
  tab; outstr (ptr); inscnt++;
} 
/**/
/**************** micCi *****************/
/*					*/
/*	 LIBRARY of Cod-generation	*/
/*					*/
/*					*/
/****************************************/
test (label)
  int label;
{         
  needbrack ("(");
  expression();
  needbrack (")");
  testjump (label); nl;
}   
store (lval)
  int *lval;
{         
  if (lval[1]==0) putmem (lval[0]);
  else putindr (lval[1]);
}   
rvalue (lval)
  int *lval;
{         
  if ((lval[0]!=0) & (lval[1]==0)) getmem (lval[0]);
  else getindr (lval[1]);
}   
getmem (sym)
  char *sym;
{         
  if ((sym[ident]!=pointer) & (sym[type]==cchar))
  { 
    outtab ("al=");
    outstr (sym+name);nl;
  } 
  else
  { 
    outtab ("ax=");
    outstr (sym+name);nl;
  } 
}   
getloc (sym)
  char *sym;
{         
  outtab ("ax=&loc");
  outdec (((sym[offset]&255) + 
   ((sym[offset+1]&255)<<8)) - 2);
  nl;
}   
putmem (sym)
  char *sym;
{         
  if ((sym[ident]!=pointer) & (sym[type]==cchar))
  {  
    outtab (sym+name);
    outstr ("=al");nl;
  }  
  else
  {  
    outtab(sym+name);
    outstr ("=ax");nl;
  }  
}   
putindr (typeobj)
  char typeobj;
{         
  if (typeobj==cchar)
  {  
    outtab ("*bx=al");nl;
  }  
  else
  {  
    outtab ("*bx=ax");nl;
  }  
}   
getindr (typeobj)
  char typeobj;
{         
  if (typeobj==cchar)
  {  
    outtab ("al=*ax, ah=sign(*ax)");nl;
  }  
  else
  {  
    outtab ("ax=*ax");nl;
  }  
}   
call (sname)
  char *sname;
{         
  outtab ("call ");
  outstr ("_"); outstr (sname);
  nl;
}   
callstk()
{         
  outtab ("call [sp]");nl;
}   
jump (label)
  int label;
{         
  outtab("GOTO ");
  outlabel (label);
  nl;
}   
testjump (label)
  int label;
{         
  outtab ("if( ax==0 ) GOTO "); outlabel(label);nl;
}   
modstk (newsp)
  int newsp;
{         
  int k;
  k=newsp-spt;
  if (k==0) return newsp;
  outtab ("sp=sp+");outdec (k);
  nl;
  return newsp;
}   
header()
{         
  outstr ("===== header =====");nl;
}  
swap(){outtab("ax <--> bx");nl;}
immed () { outtab ("ax="); }
immedl() { outtab ("ax=&"); }
push(){outtab("push ax");nl;}
pop(){outtab ("pop bx");nl; }
popbpsp()
{ 
  outtab ("sp=bp");nl;
  outtab ("pop bp");nl;
} 
pushbpsp()
{ 
  outtab ("push bp");nl;
  outtab ("bp=sp");nl;
} 
ret   () { outline ("return"); }
scale()
{         
  outtab ("ax=ax*2");nl;
}   
add(){ outtab ("ax=bx+ax");nl;}
sub()
{         
  outtab ("ax=bx-ax");nl;
}   
mult(){outtab("ax=bx*ax");nl; }
div   ()
{ 
  outtab ("ax=bx/ax");nl;
} 
mod()
{         
  outtab ("ax=bx%ax");nl;
}   
or(){outtab("ax=bx|ax");nl;}
xor(){outtab("ax=bx^ax");nl;}
and(){outtab ("ax=bx&ax");nl;}
asr()
{         
  outtab ("ax=bx/(2**ax)");nl;
}   
asl()
{         
  outtab ("ax=bx*(2**ax)");nl;
}   
neg() { outtab ("ax=-ax");nl; }
inc (n)
  int n;
{         
  outtab ("ax=ax+"); outdec (n);nl;
}   
dec (n)
  int n;
{         
  outtab ("ax=ax-"); outdec (n);nl;
}   
tst(ptr)
  int ptr[];
{         
  outtab ("if( bx ");outstr (ptr);
  outstr (" ax ) ax=1; else ax=0;");nl;
}   
dumplits()
{         
  int j,k;
  /**/
  if (litptr==0) return;
  k=0;
  outlabel (litlab); colon;nl;
  while (k<litptr)
  {  
    outstr (quoch1);
    while (outbyte (litq[k++]));
    outstr(quoch1);outstr (",0");nl;
  }  
}   
dumpglbs()
{         
  int j;
  /**/
  cptr=startglb;
  while (cptr<glbptr)
  {  
    if (cptr[ident]!=function)
    {  
      outstr (cptr); colon;outstr(" [");
      j=((cptr[offset]&255) +
        ((cptr[offset+1]&255)<<8));
      if ((cptr[type]==cint) |
          (cptr[ident]==pointer))
           j=j*intwidth;
      outdec (j); outstr ("]");
      nl;
    }  
    cptr=cptr+symsiz;
  }  
}   
trailer()
{         
  outstr ("===== trailer =====");nl;
}   
/***************************************/
/*           end of compiler           */
/***************************************/
setarg(argv)
  int *argv[];
{ 
  #asm
  mov   si,4
  mov   bx,word ptr [bp+4]
  mov   cx,42h
  mov   di,82h
  mov   word ptr cs:[bx+2],di
  mov   ax,1
  cmp   byte ptr cs:[di-2],0h
  jne   sarg1
  mov   ax,0
  jmp short sarg4
sarg1:
  inc   di
  cmp   byte ptr cs:[di-1],0dh
  je    sarg3
  cmp   byte ptr cs:[di-1],20h
  jne   sarg2
  mov   byte ptr cs:[di-1],0
  mov   word ptr cs:[bx+si],di
  inc   si
  inc   si
sarg2:
  loop  sarg1
sarg3:
  mov   byte ptr cs:[di-1],0
sarg4:
  #endasm
} 
exit()
{ 
  #asm
  mov ah,4ch
  int 21h
  #endasm
} 
/******************/
fopen(file,typ1)/* must be prepare File_Buf,File_ds */
  int *file,typ1;
{ 
  #asm
  push  ds
  mov   ax,cs
  add   ax,1000h
  mov   word ptr cs:File_ds,ax
;*
  mov   dx,word ptr [bp+6]
  mov   al,byte ptr [bp+4]
  mov   cx,0
  cmp   word ptr [bp+4],0
  je    fopen4
  mov   ah,3ch
  int   21h		; @Create
  jc    fopen2
  jmp   short fopen5
fopen4:
  mov   ah,3dh
  int   21h		; @Open
  jc    fopen2
fopen5:
  mov	bx,ax
  sub	bx,5
  mov	ax,400h
  mul   bx
  add   ax,word ptr cs:File_ds
  mov	ds,ax
  mov	word ptr ds:[0],4
  mov	word ptr ds:[2],3
  cmp	word ptr [bp+4],0
  je	fopen3
  mov	word ptr ds:[0],0
fopen3:
  add	bx,5
  mov   ax,bx
  jmp short fopen1
fopen2:
  mov   ax,0
fopen1:
  pop	ds
  #endasm
} 
fclose(handle)
  int handle;
{ 
  #asm
  push  ds
  mov   bx,word ptr [bp+4]
  sub	bx,5
  mov   ax,400h
  mul	bx
  add   ax,word ptr cs:File_ds
  mov   ds,ax
  cmp	word ptr ds:[0],0
  jne	fclose1
;*
  mov   bx,word ptr [bp+4]
  mov   dx,4
  mov   cx,word ptr ds:[2]
  sub	cx,3
  mov   ah,40h
  int   21h		; @Write
fclose1:
  mov   bx,word ptr [bp+4]
  mov   ah,3eh
  int   21h		; @Close
  pop	ds
  #endasm
} 
fgetc(handle)
  int handle;
{ 
  #asm
  push  ds
  mov   bx,word ptr [bp+4]
  sub	bx,5
  mov   ax,400h
  mul	bx
  add   ax,word ptr cs:File_ds
  mov   ds,ax
fgetc5:
  cmp	word ptr ds:[2],3
  jne	fgetc1
;*
  mov   bx,word ptr [bp+4]
  mov   dx,4
  mov   cx,3ffch
  mov   ah,3fh
  int   21h		; @Read
  jc	fgetc4
  and	ax,3fffh
  mov	word ptr ds:[2],4
  add	ax,4
  mov	word ptr ds:[0],ax
fgetc1:
  mov	si,word ptr ds:[2]
  mov	al,byte ptr ds:[si]
  xor	ah,ah
  inc	si
  mov	word ptr ds:[2],si
  cmp	al,0dh
  je	fgetc5			; for compile - LF
  cmp	si,word ptr ds:[0]
  jc	fgetc2
  mov	word ptr ds:[2],3
  test	word ptr ds:[0],3fffh
  jz	fgetc3
fgetc4:
  mov	word ptr ds:[2],0	; yes feof !
fgetc3:
  mov	word ptr ds:[0],4
fgetc2:
  pop	ds
  #endasm
} 
fputc (c,handle)
  char c;int handle;
{ 
  #asm
  push  ds
  mov   bx,word ptr [bp+4]
  sub	bx,5
  mov   ax,400h
  mul	bx
  add   ax,word ptr cs:File_ds
  mov   ds,ax
fputc2:
  mov	si,word ptr ds:[2]
  inc	si
  mov	word ptr ds:[2],si
  cmp	si,4000h
  jc	fputc1
;*
  mov   bx,word ptr [bp+4]
  mov   dx,4
  mov   cx,3ffch
  mov   ah,40h
  int   21h		; @Write
  mov	word ptr ds:[0],0
  mov	word ptr ds:[2],3
  jmp short fputc2
fputc1:
  mov	al,byte ptr [bp+6]
  xor	ah,ah
  mov	byte ptr ds:[si],al
  pop	ds
  #endasm
} 
feof(handle)
  int handle;
{ 
  #asm
  push  ds
  mov   bx,word ptr [bp+4]
  sub	bx,5
  mov   ax,400h
  mul	bx
  add   ax,word ptr cs:File_ds
  mov   ds,ax
;*
  mov	ax,0
  cmp	word ptr ds:[2],0
  jne	feof1
  mov	ax,1
feof1:
  pop	ds
  #endasm
} 
getchar()
{ 
  #asm
  mov   ah,01h
  int   21h
  xor   ah,ah
  #endasm
} 
putchar(c)
  int c;
{ 
  #asm
  mov   dl,byte ptr [bp+4]
  mov   ah,02h
  int   21h
  #endasm
}  
 