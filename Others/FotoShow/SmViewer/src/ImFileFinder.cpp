
/*

#include "ImFileFinder.h"
#include "f32file.h"

_LIT(KRootFolder,"c:\\");
_LIT(KWildName,"*.jpg");

CImFileFinder* CImFileFinder::NewL()
    {
    CImFileFinder* self = NewLC();
    CleanupStack::Pop(self);
    return self;
    }

RThread    

CImFileFinder* CImFileFinder::NewLC()
    {
    CImFileFinder* self = new (ELeave) CImFileFinder();
    CleanupStack::PushL(self);
    self->ConstructL();
    return self;
    }


CImFileFinder::CImFileFinder()
{
	iIdle = 0;
}


CImFileFinder::~CImFileFinder()
{
   iIdle->Cancel();
}


void CImFileFinder::ConstructL()
{
	iScanDir = KRootFolder;
	iIdle = CIdle::NewL(CIdle::EPriorityStandard);
}


void CImFileFinder::FindImages(TAny* iThis)
{
	
}


void CImFileFinder::StartFindImages()
{
//	iWaitDlg = 0;
//	iWaitDlg = new(ELeave) CAknWaitDialog(NULL, ETrue);//REINTERPRET_CAST(CEikDialog**,&waitDlg));
//	iWaitDlg->SetCallback(this);
//	TRAPD(err,iWaitDlg->ExecuteLD(R_WAIT_NOTE));

	iIdle->Start(FindImages,this);
		
	ForAllMatchingFiles(CEikonEnv::Static()->FsSession(),KWildName,KRootFolder);
	

//    CCoeAppUi* ui = STATIC_CAST(CCoeAppUi*, iCoeEnv->AppUi()); 
//    if (ui->IsDisplayingMenuOrDialog()) // Check whether wait note is displayed.
//    {
//        TKeyEvent key;
//        key.iCode = EKeyEscape;
//        key.iModifiers = 0;
//        iCoeEnv->SimulateKeyEventL(key, EEventKey);
//    }

//	UpdateScrollBar();
//	iWaitDlg  = 0;
}








void CImFileFinder::RunL()
{
	

	TFullName aScanDir(iScanDir);
	CCoeEnv* env = iObserver->Environment();
	RFs& aSession = env->FsSession();

	TFindFile file_finder(aSession);
    CDir* file_list=0; 
    TInt err = file_finder.FindWildByDir(KWildName,aScanDir, file_list);
    if(err==KErrNone)
        {
        TInt i;
        for (i=0; i<file_list->Count(); i++)
         {
			TEntry entry = (*file_list)[i];
			if(entry.iSize!=2468)
			{
				TBuf<KMaxFileName> file = aScanDir;
				file.Append(entry.iName);
				iObserver->AddItem(file);
			}
         }
        }

    delete file_list;

    CDir* dirs_list=0; 
	err = file_finder.FindWildByDir(_L("*"),aScanDir, dirs_list);
    if(err==KErrNone)
        {
        TInt i;
        for (i=0; i<dirs_list->Count(); i++)
        {
			if((*dirs_list)[i].IsDir())
			{
				if(iRunLProgress==KErrCancel)
					break;
				TParse fullname;
				fullname.Set(iScanDir,NULL,NULL);
				fullname.AddDir((*dirs_list)[i].iName);
				iScanDir = fullname.DriveAndPath();
				iRunLProgress = KRequestPending;
				SetActive();
				iRunLProgress = KRequestPending;
				User::WaitForRequest(iRunLProgress);
			}
        }
        }

    delete dirs_list;

	TRequestStatus* req = &iRunLProgress;
	User::RequestComplete(req,KErrNone);

}






void CImFileFinder::StartFinder(CAknColumnListBox* aListBox,CSmViewerContainer* aObserver)
{

	iListBox = aListBox;
	iObserver = aObserver;
	iRunLProgress = 0;
	iStatus = KRequestPending;
	SetActive();
	TBool add = IsAdded();

//	iWaitDlg = 0;
//	iWaitDlg = new(ELeave) CAknWaitDialog(NULL, ETrue);//REINTERPRET_CAST(CEikDialog**,&waitDlg));
//	iWaitDlg->SetCallback(this);
//	TRAPD(err,iWaitDlg->ExecuteLD(R_WAIT_NOTE));
	
//	ForAllMatchingFiles(,KWildName,KRootFolder);
//    CCoeAppUi* ui = STATIC_CAST(CCoeAppUi*, iCoeEnv->AppUi()); 
  //  if (ui->IsDisplayingMenuOrDialog()) // Check whether wait note is displayed.
//    {
//        TKeyEvent key;
//        key.iCode = EKeyEscape;
//        key.iModifiers = 0;
//        iCoeEnv->SimulateKeyEventL(key, EEventKey);
//    }

//	UpdateScrollBar();
//	iWaitDlg  = 0;

}
*/
