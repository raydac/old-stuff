// Конвертор текстовых файлов в бинарный формат описателя COP
program txt2rcp;

uses
  classes,sysutils;

const
 // Кнопки
 ADDR_CB00 = 00; NAME_CB00 = 'CB00';
 ADDR_CB01 = 01; NAME_CB01 = 'CB01';
 ADDR_CB02 = 02; NAME_CB02 = 'CB02';
 ADDR_CB03 = 03; NAME_CB03 = 'CB03';
 ADDR_CB04 = 04; NAME_CB04 = 'CB04';
 ADDR_CB05 = 05; NAME_CB05 = 'CB05';
 ADDR_CB06 = 06; NAME_CB06 = 'CB06';
 ADDR_CB07 = 07; NAME_CB07 = 'CB07';
 ADDR_CB08 = 08; NAME_CB08 = 'CB08';
 ADDR_CB09 = 09; NAME_CB09 = 'CB09';
 ADDR_CB10 = 10; NAME_CB10 = 'CB10';
 ADDR_CB11 = 11; NAME_CB11 = 'CB11';
 ADDR_CB12 = 12; NAME_CB12 = 'CB12';
 ADDR_CB13 = 13; NAME_CB13 = 'CB13';
 ADDR_CB14 = 14; NAME_CB14 = 'CB14';
 ADDR_CB15 = 15; NAME_CB15 = 'CB15';
 ADDR_CB16 = 16; NAME_CB16 = 'CB16';
 ADDR_CB17 = 17; NAME_CB17 = 'CB17';
 ADDR_CB18 = 18; NAME_CB18 = 'CB18';
 ADDR_CB19 = 19; NAME_CB19 = 'CB19';
 ADDR_CB20 = 20; NAME_CB20 = 'CB20';
 ADDR_CB21 = 21; NAME_CB21 = 'CB21';
 ADDR_CB22 = 22; NAME_CB22 = 'CB22';
 ADDR_CB23 = 23; NAME_CB23 = 'CB23';
 ADDR_CB24 = 24; NAME_CB24 = 'CB24';
 ADDR_CB25 = 25; NAME_CB25 = 'CB25';
 ADDR_CB26 = 26; NAME_CB26 = 'CB26';
 ADDR_CB27 = 27; NAME_CB27 = 'CB27';
 ADDR_CB28 = 28; NAME_CB28 = 'CB28';
 ADDR_CB29 = 29; NAME_CB29 = 'CB29';
 ADDR_CB30 = 30; NAME_CB30 = 'CB30';
 ADDR_CB31 = 31; NAME_CB31 = 'CB31';
 ADDR_DOB  = 32; NAME_DOB = 'DOB';
 ADDR_DCB  = 33; NAME_DCB  = 'DCB';
 ADDR_BELLB= 34; NAME_BELLB  = 'BELLB';

// Ключи
 ADDR_ESK  = 64; NAME_ESK  = 'ESK';
 ADDR_ISS  = 65; NAME_ISS  = 'ISS';

// Индикаторы
 ADDR_CUDL = 128; NAME_CUDL= 'CUDL';
 ADDR_CDDL = 129; NAME_CDDL= 'CDDL';
 ADDR_OLS  = 130; NAME_OLS = 'OLS';

// Разное
 ADDR_BUZ  = 192; NAME_BUZ = 'BUZ';

type
 TIntArray = array [0..1023] of integer;
 PIntArray = ^TIntArray;

var
 RCont : TStrings;
 COP_Name : Pointer;
 COP_Array : Pointer;

function PackInt (APort,ABit : integer) : integer;
begin
 result := (APort shl 16) or (1 shl ABit);
end;

procedure GetPortValue(AStr : string; var AAddr,APort,ABit : integer; var AErr : string);
var
 liname : string;
 liport : string;
 libit  : string;
 litmp  : string;
 li,lii,lsport,lsbit: integer;
begin
  AErr := '';
  AStr := UpperCase(Trim(AStr));
  if length(AStr)=0 then exit;
  if (AStr[1]='#') then exit;

  litmp := '';
  for li:=1 to length(AStr) do
   if AStr[li]='#' then break else litmp := litmp+AStr[li];
  AStr := litmp;
  if length(AStr)=0 then exit;

  liport := ''; liname:=''; libit:=''; litmp:='';
  lii := 0;
  for li:=1 to length(AStr) do
  begin
   case lii of
    0 : begin
         if AStr[li]=' ' then lii:=1 else liname:=liname+AStr[li];
        end;
    1 : begin
         if AStr[li]=',' then lii:=2 else liport:=liport+AStr[li];
        end;
    2 : libit:=libit+AStr[li];
    end;
  end;
 liname := trim(liname);
 liport := trim(liport);
 libit  := trim(libit);

 try
  lsport := StrToInt(liport);
  if (lsport<0) or (lsport>63) then raise EConvertError.Create('Error');
 except
  AErr := 'Error in port number!';
  exit;
 end;

 try
  lsbit := StrToInt(libit)-1;
  if (lsbit>4) or (lsbit<0) then raise EConvertError.Create('Error');
 except
  AErr := 'Error in bit number!';
 end;

 if liname=NAME_CB00 then lii := ADDR_CB00 else
 if liname=NAME_CB01 then lii := ADDR_CB01 else
 if liname=NAME_CB02 then lii := ADDR_CB02 else
 if liname=NAME_CB03 then lii := ADDR_CB03 else
 if liname=NAME_CB04 then lii := ADDR_CB04 else
 if liname=NAME_CB05 then lii := ADDR_CB05 else
 if liname=NAME_CB06 then lii := ADDR_CB06 else
 if liname=NAME_CB07 then lii := ADDR_CB07 else
 if liname=NAME_CB08 then lii := ADDR_CB08 else
 if liname=NAME_CB09 then lii := ADDR_CB09 else
 if liname=NAME_CB10 then lii := ADDR_CB10  else
 if liname=NAME_CB11 then lii := ADDR_CB11 else
 if liname=NAME_CB12 then lii := ADDR_CB12 else
 if liname=NAME_CB13 then lii := ADDR_CB13 else
 if liname=NAME_CB14 then lii := ADDR_CB14 else
 if liname=NAME_CB15 then lii := ADDR_CB15 else
 if liname=NAME_CB16 then lii := ADDR_CB16 else
 if liname=NAME_CB17 then lii := ADDR_CB17 else
 if liname=NAME_CB18 then lii := ADDR_CB18 else
 if liname=NAME_CB19 then lii := ADDR_CB19 else
 if liname=NAME_CB20 then lii := ADDR_CB20 else
 if liname=NAME_CB21 then lii := ADDR_CB21 else
 if liname=NAME_CB22 then lii := ADDR_CB22 else
 if liname=NAME_CB23 then lii := ADDR_CB23 else
 if liname=NAME_CB24 then lii := ADDR_CB24 else
 if liname=NAME_CB25 then lii := ADDR_CB25 else
 if liname=NAME_CB26 then lii := ADDR_CB26 else
 if liname=NAME_CB27 then lii := ADDR_CB27 else
 if liname=NAME_CB28 then lii := ADDR_CB28 else
 if liname=NAME_CB29 then lii := ADDR_CB29 else
 if liname=NAME_CB30 then lii := ADDR_CB30 else
 if liname=NAME_CB31 then lii := ADDR_CB31 else
 if liname=NAME_DOB  then lii := ADDR_DOB  else
 if liname=NAME_DCB  then lii := ADDR_DCB  else
 if liname=NAME_BELLB then lii:= ADDR_BELLB else

 if liname=NAME_ESK  then lii := ADDR_ESK  else
 if liname=NAME_ISS  then lii := ADDR_ISS  else

 if liname=NAME_CUDL then lii := ADDR_CUDL else
 if liname=NAME_CDDL then lii := ADDR_CDDL else
 if liname=NAME_OLS  then lii := ADDR_OLS  else

 if liname=NAME_BUZ  then lii := ADDR_BUZ  else AErr := 'Unknown keyword!';

 AAddr := lii;
 APort := lsport;
 ABit := lsbit;
end;

procedure SetCOPName(AName : string);
var
 li : integer;
begin
 for li:=0 to 256 do PIntArray(COP_Array)[li]:=PackInt(1,4);
 for li:=0 to 15 do
 begin
  if (li>=length(AName)) then
   begin
    PByteArray(COP_Name)[li]:=32;
   end
  else
   begin
    PByteArray(COP_Name)[li]:=byte(AName[li+1]);
   end;
 end;

end;

procedure SaveRCPFile;
var
 OutFile : TFileStream;
begin
try
 OutFile := TFileStream.Create(ParamStr(2),fmCreate or fmShareExclusive);
 OutFile.Write(COP_Name^,16);
 OutFile.Write(COP_Array^,1024);
 OutFile.Free;
except
 writeln('Error of out file');
end;
end;

var
 lnaddr,lni,lnport,lnbit : integer;
 lnerr : string;

begin
 if ParamCount<>2 then
  begin
   Writeln('Конвертер текстовых файлов в формат COP.');
   Writeln('Формат: txt2rcp.exe file_name_src file_name_dst');
  end
 else
  begin
   RCont := nil;
   RCont := TStringList.Create;
   GetMem(COP_Array,1024);
   GetMem(pointer(COP_Name),16);
   try
    RCont.LoadFromFile(ParamStr(1));
    if RCont.Count<2 then raise Exception.Create('');
   except
    Writeln('Ошибка загрузки исходного файла');
    if Assigned(RCont) then RCont.Free;
    exit;
   end;
   SetCOPName(RCont.Strings[0]);
   for lni:=1 to RCont.Count-1 do
   begin
     GetPortValue(RCont.Strings[lni],lnaddr,lnport,lnbit,lnerr);
     if length(lnerr)>0 then
      begin
       writeln(lnerr+' in '+IntToStr(lni)+' string');
       if Assigned(RCont) then RCont.Free;
       exit;
      end;
     PIntArray(COP_Array)[lnaddr] := PackInt(lnport,lnbit);
   end;

   SaveRCPFile;
   if Assigned(RCont) then RCont.Free;
  end;
end.
