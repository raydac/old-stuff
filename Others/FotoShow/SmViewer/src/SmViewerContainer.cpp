/*
* ============================================================================
*  Name     : CSmViewerContainer from SmViewerContainer.h
*  Part of  : SmViewer
*  Created  : 05/09/2003 by 
* ============================================================================
*/
/* ========================================================================
  * Author:  Oleg Golosovskiy, http://www.sw4u.org mailto:mail@sw4u.org
  * ======================================================================== */
/** 
  * \author Oleg Golosovsky     
  */


// INCLUDE FILES
#include <akntitle.h>
#include "SmViewerContainer.h"
#include <SmViewer.rsg>
#include <barsread.h>
#include <eikclbd.h>  
#include <avkon.hrh>  
#include <gulicon.h>  
#include <SMVIEWER.mbg>
#include <redircli.h>
#include <aknquerydialog.h>
#include <eikprogi.h>
#include "SmViewerContainer3.h"
#include <AknGMSStyleGrid.h>

const TInt KFullScreenHeight = 188;
const TInt KHeaderSize = 1024;
const TInt KBytesPerPixel = 3;

_LIT(KSmViewerMbmFileName, "\\system\\apps\\SMVIEWER\\SMVIEWER.MBM");
_LIT(KRootFolder,"c:\\");
_LIT(KRootFolder2,"e:\\");
_LIT(KWildNameJPG,"*.jpg");
_LIT(KWildNameOTA,"*.ota");
_LIT(KWildNameGIF,"*.gif");
_LIT(KWildNameTIFF,"*.tiff");
//_LIT(KWildNameMBM,"*.mbm");
_LIT(KWildNameWBMP,"*.wbmp");
_LIT(KWildNameWMF,"*.wmf");
_LIT(KWildNamePNG,"*.png");


// the hight of GMS grid
const TInt KHightOfGrid = 139;
// the width of GMS grid
const TInt KWidthOfGrid = 161;
// the top horizontal value in relation to its parent's top side
const TInt KTopOfGrid = 2;
// the left vertical value in relation to its parent's left side
const TInt KLeftOfGrid = 8;
// the color index number
const TInt KColorIndex = 0;

//#define R_QTN_ALBUM_IMAGE_FORMAT_JPG              0x4d24f055
//#define R_QTN_ALBUM_IMAGE_FORMAT_GIF              0x4d24f056
//#define R_QTN_ALBUM_IMAGE_FORMAT_PNG              0x4d24f057
//#define R_QTN_ALBUM_IMAGE_FORMAT_TIFF             0x4d24f058
//#define R_QTN_ALBUM_IMAGE_FORMAT_MBM              0x4d24f059
//#define R_QTN_ALBUM_IMAGE_FORMAT_BMP              0x4d24f05a
//#define R_QTN_ALBUM_IMAGE_FORMAT_WBMP             0x4d24f05b
//#define R_QTN_ALBUM_IMAGE_FORMAT_OTA              0x4d24f05c
//#define R_QTN_ALBUM_IMAGE_FORMAT_WMF

_LIT(KTxtShared,"FindImagesThread");



CSmViewerContainer::CSmViewerContainer() 
{
	iAdder =0;
}

// ================= MEMBER FUNCTIONS =======================

// ---------------------------------------------------------
// CSmViewerContainer::ConstructL(const TRect& aRect)
// EPOC two phased constructor
// ---------------------------------------------------------
//
void CSmViewerContainer::ConstructL(const TRect& aRect,CSmViewerAppUi* aAppUi,TSmViewerSettings::TListBoxType aType)
{
	iPercent=0;
	iSaver=0;

	iRenamedArray=0;


	iNaviDecorator=0;

	if(TSmViewerSettings::EGrid== aType)
		iGrid = ETrue;
	else
		iGrid = EFalse;


	
	iImage = 0;
    CreateWindowL();

	iImage = new (ELeave) CEikImage();
    iImage ->SetContainerWindowL( *this );

	if(iImage)
	{
        CleanupStack::PushL(iImage);   
//		iSplIcon = iEikonEnv->CreateBitmapL(const TDesC& aFileName,KSmViewerMbmFileName, EMbmSmviewerSplash, EMbmSmviewerSplash );
		CFbsBitmap* bitmap = 0;
		bitmap = iEikonEnv->CreateBitmapL(KSmViewerMbmFileName,EMbmSmviewerSplash);
		if(bitmap)
			iImage->SetPicture(bitmap, bitmap);
		else
			User::Leave(KErrNotFound);
        CleanupStack::Pop(); // iImage
	}

            // Set Progress Bar property, using resource

	iSplashWindow=ETrue;

	iAppUi = aAppUi;


	iFactory=0;
	iFactory = CPAlbImageFactory::NewL(this);
	iImageData = CPAlbImageData::NewL();

	iListBox=0;
    SetRect(aRect);


	iIcons = new( ELeave ) CAknIconArray(2);
//0
	iIcons->AppendL(iEikonEnv->CreateIconL( KSmViewerMbmFileName, EMbmSmviewerNoimage, EMbmSmviewerNoimage ) );
//1
	iIcons->AppendL(iEikonEnv->CreateIconL( KSmViewerMbmFileName, EMbmSmviewerTellink, EMbmSmviewerTellink_mask ) );
//2
	iIcons->AppendL(iEikonEnv->CreateIconL( KSmViewerMbmFileName, EMbmSmviewerNotellink, EMbmSmviewerNotellink ) );
//3
	iIcons->AppendL(iEikonEnv->CreateIconL( KSmViewerMbmFileName, EMbmSmviewerMarked_add, EMbmSmviewerMarked_add ) );
//4
//	iIcons->AppendL(iEikonEnv->CreateIconL( KSmViewerMbmFileName, EMbmSmviewerCheck_box, EMbmSmviewerCheck_box ) );
	



	ConstructListBoxL();
	
	
	TCallBack callback(CSmViewerContainer::AddItem,this);
	iAdder = new (ELeave) CAsyncCallBack(callback,CActive::EPriorityStandard);


	iThumbTimer = CPeriodic::NewL(CActive::EPriorityStandard);


	CEikButtonGroupContainer * pCbaGroup = CEikButtonGroupContainer::Current();
	pCbaGroup->SetCommandSetL(R_AVKON_SOFTKEYS_EXIT);
	pCbaGroup->DrawNow();


// подсчет каталогов второго уровня
	


	iCatalogLevel=0;
	iCatalogCounter=0;
	CatalogsCounter(iCoeEnv->FsSession(),KRootFolder);
	TInt err;
	err=iCoeEnv->FsSession().ScanDrive(_L("E:"));
	if((KErrNone == err)||(KErrInUse == err))
		CatalogsCounter(iCoeEnv->FsSession(),KRootFolder2); 


	SetCaptionL();

	StartThreadForFindImages();

	SetCaptionL(EFalse);
}

void CSmViewerContainer::SetListView(TBool aLIst)
{
	iGrid = !aLIst;
	ConstructListBoxL();
	DrawNow();
}

void CSmViewerContainer::ConstructListBoxL()
{
	CListImagesArray* curarr=0;
	if(iTextArray)
	{
		curarr = new (ELeave) CListImagesArray(2);
		for(TInt i=0;i<iTextArray->Count();i++)
		{
			TImageInfo info(iTextArray->At(i));
			curarr->AppendL(info);
		}
		
	}

	if(iListBox)
	{
		if(iGrid)
			((CAknDoubleLargeStyleListBox*)iListBox)->ItemDrawer()->ColumnData()->SetIconArray(0);
		else// CAknGMSStyleGrid
			((CAknGMSStyleGrid*)iListBox)->ItemDrawer()->FormattedCellData()->SetIconArrayL(0);
	}

	delete iListBox;
	iListBox=0;

	iTextArray = 0;

	if(curarr!=0)
		iTextArray = curarr;
	else
		iTextArray = new (ELeave) CListImagesArray(2);


    TResourceReader reader;


	if(!iGrid)
		iListBox = new (ELeave) CAknDoubleLargeStyleListBox;
	else
	{
		iListBox = CAknGMSStyleGrid::NewL(this);
//		iListBox = new (ELeave ) CAknGMSStyleGrid;//::NewL(this);
//		((CAknGMSStyleGrid*)iListBox)->ConstructL(this,4,2);
	}
	
    iListBox->SetContainerWindowL( *this );
	if(!iGrid)
		iListBox->SetListBoxObserver(this);


	if(!iGrid)
	{
	    CEikonEnv::Static()->CreateResourceReaderLC( reader,R_LIST_SINGLE_LARGE_GRAPHIC);
	    iListBox->ConstructFromResourceL( reader );
		CleanupStack::PopAndDestroy(); // resource stuffs.
	}

    // Creates scrollbar.
    //CreateScrollbarL( aListBox );
	
	SetGraphicIconL();


	if(!iGrid)
		((CAknDoubleLargeStyleListBox*)iListBox)->Model()->SetItemTextArray(iTextArray);
	else//CAknGMSStyleGrid
		((CAknGMSStyleGrid*)iListBox)->Model()->SetItemTextArray(iTextArray);


	for(TInt i=0;i<iTextArray->Count();i++)
	{
		CreateText((*iTextArray)[i]);
	}



	iListBox->SetCurrentItemIndex(0);
	CreateScrollbarL();
    
	if(iGrid)
	{
		((CAknGMSStyleGrid*)iListBox)->SetupLayout();
//        ((CAknGMSStyleGrid*)iListBox)->SetItemHeightL(48);
	}


	SizeChanged();

	ActivateL();
}
	
void CSmViewerContainer::CatalogsCounter(RFs& aSession, const TDesC& aScanDir)
{
	iCatalogLevel++;


 		TFindFile dir_finder(aSession);
        CDir* dirs_list=0; 
    	TInt err = dir_finder.FindWildByDir(_L("*"),aScanDir, dirs_list);
        if(err==KErrNone)
            {
            TInt i;
            for (i=0; i<dirs_list->Count(); i++)
            {
    			if((*dirs_list)[i].IsDir())
    			{
					TParse fullentry;
					fullentry.Set(aScanDir,NULL,NULL); // 5,6,7
					fullentry.AddDir((*dirs_list)[i].iName);
					if(iCatalogLevel==2)
						iCatalogCounter++;
					if(iCatalogLevel<2)
					{
    					CatalogsCounter(aSession,fullentry.FullName());
					}
    			}
            }
            }
    
        delete dirs_list;

	iCatalogLevel--;
    
}

void CSmViewerContainer::CreateScrollbarL()
    {
    if ( iListBox )
        {
        // Creates scrollbar.
        iListBox->CreateScrollBarFrameL( ETrue );
        iListBox->ScrollBarFrame()->SetScrollBarVisibilityL(
            CEikScrollBarFrame::EOff, CEikScrollBarFrame::EAuto );
        }
    }


void CSmViewerContainer::HandleListBoxEventL(CEikListBox* /*aListBox*/, TListBoxEvent aEventType)
{

	iThumbTimer->Cancel();

	if(aEventType==EEventEnterKeyPressed)
		ActivateItem(EFalse);

}


void CSmViewerContainer::ActivateItem(TBool aRunSlide,TBool aSelected/* =EFalse */)
{
		iThumbTimer->Cancel();

		TInt ind = 0;
		
		TInt selInd=0;

		if(aSelected)
		{
			ind = iListBox->CurrentItemIndex();

			for(TInt i=0;i<iTextArray->Count();i++)
				if(iTextArray->At(i).iSelected!=EFalse)
				{
					if(ind==i)
					{
						ind = selInd;
					}
					selInd++;
				}
			
		}
		else
			ind = iListBox->CurrentItemIndex();

		iStopShowIndex = -1;

#ifdef _DEBUG
		TImageInfo inf = iTextArray->At(ind);
#endif

		iAppUi->ShowImage(ind,aRunSlide,aSelected);
}

void CSmViewerContainer::ActivateItemLink()
{
		iStopShowIndex = -1;
		TInt ind = iListBox->CurrentItemIndex();
		iAppUi->ShowLink(ind);
}


TInt CSmViewerContainer::SelectedItems()
{
	TInt sel=0;
	for(TInt i=0;i<iTextArray->Count();i++)
	{
		if(iTextArray->At(i).iSelected!=EFalse)
			sel++;
	}

	return sel;
}

void CSmViewerContainer::Delete()
{

	TInt i=0;
	for(i=0;i<iTextArray->Count();i++)
	{
		if(iTextArray->At(i).iSelected!=EFalse)
			break;
	}

	if(i<iTextArray->Count())	// есть выбранные
	{
		// диалог
		if(iAppUi->ShowConfirmation(EDeleteAllSelectedItems))
		{
			DeleteAllSelected();
			SelectAllItem(EFalse);
		}

	}
	else// нет выбранных
	{
		// диалог
		if(iAppUi->ShowConfirmation(EDeleteCurrentItem))
			DeleteCurrent();
	}



	if(iListBox->CurrentItemIndex()==-1)
		if(iTextArray->Count()>0)
			iListBox->SetCurrentItemIndexAndDraw(iTextArray->Count()-1);

	iListBox->HandleItemAdditionL();

	SetCaptionL();

}


void CSmViewerContainer::SelectItem(TInt aIndex,TBool aSelect)
{
	if(aIndex<iTextArray->Count())
	{
		(*iTextArray)[CurrentItem()].iSelected=aSelect;
		CreateText((*iTextArray)[aIndex]);
		SetCaptionL();
		DrawNow();
	}
}


void CSmViewerContainer::SelectAllItem(TBool aSelect)
{
	for(TInt Index=0;Index<iTextArray->Count();Index++)
	{
		(*iTextArray)[Index].iSelected=aSelect;
		CreateText((*iTextArray)[Index]);
	}
	SetCaptionL();
	DrawNow();

}

void CSmViewerContainer::SelectPHItem()
{
	for(TInt Index=0;Index<iTextArray->Count();Index++)
	{
		if(
			(((*iTextArray)[Index].iFileName.FullName())[0]=='c')||
			(((*iTextArray)[Index].iFileName.FullName())[0]=='C')
			)
		{
			(*iTextArray)[Index].iSelected=ETrue;
			CreateText((*iTextArray)[Index]);
		}
	}
	SetCaptionL();
	DrawNow();
}

void CSmViewerContainer::SelectMCItem()
{
	for(TInt Index=0;Index<iTextArray->Count();Index++)
	{
		if(
			(((*iTextArray)[Index].iFileName.FullName())[0]=='e')||
			(((*iTextArray)[Index].iFileName.FullName())[0]=='E')
			)
		{
			(*iTextArray)[Index].iSelected=ETrue;
			CreateText((*iTextArray)[Index]);
		}
	}
	SetCaptionL();
	DrawNow();
}

TBool CSmViewerContainer::IsCurrentItemSelected()
{
	if(iListBox!=0) 
	{
		return iTextArray->At(CurrentItem()).iSelected;

	}

	return EFalse;
}

TBool CSmViewerContainer::IsSelectedItems()
{
	TInt i=0;
	if(iListBox!=0) 
	{
		for(i=0;i<iTextArray->Count();i++)
			if(iTextArray->At(i).iSelected!=EFalse)
				break;
	}

	if(i<iTextArray->Count())
		return ETrue;

	return EFalse;
}

/*
void CSmViewerContainer::SelectItemLink(TInt aIndex)
{
	if(aIndex<iTextArray->Count())
	{
		CreateText((*iTextArray)[aIndex]);
		SetCaptionL();
		DrawNow();

	}
}

void CSmViewerContainer::SelectAllItemLinks()
{
	for(TInt Index=0;Index<iTextArray->Count();Index++)
	{
		CreateText((*iTextArray)[Index]);
	}

	SetCaptionL();
	DrawNow();

}*/



void CSmViewerContainer::UpdateScrollBar()
    {
    if (iListBox)
        {   
        TInt pos(iListBox->View()->CurrentItemIndex());
        if (iListBox->ScrollBarFrame())
            {
            iListBox->ScrollBarFrame()->MoveVertThumbTo(pos);
            }
        }
    }


TKeyResponse  CSmViewerContainer::OfferKeyEventL(const TKeyEvent& aKeyEvent,TEventCode aType )
{
    if ( aType != EEventKey ) // Is not key event?
        {
        return EKeyWasNotConsumed;
        }


	if(IsInProgress())
		return EKeyWasNotConsumed;


    if ( iListBox )
		if ( iTextArray->Count()>0 )
        {
		  switch ( aKeyEvent.iCode )
			{
			case EKeyLeftArrow:
				{
					if(!iGrid)	
					{
						SelectItem(CurrentItem(),EFalse);
						return EKeyWasConsumed;
					}
				}
				break;
			case EKeyRightArrow:
				{
					if(!iGrid)	
					{
						SelectItem(CurrentItem(),ETrue);
						return EKeyWasNotConsumed;
					}
				}
				break;
			case EKeyDevice3:
				{
					ActivateItem(EFalse);
				}
				return EKeyWasConsumed;
			default:
				{
				}
			}


			iThumbTimer->Cancel();
			TKeyResponse key;
			key = iListBox->OfferKeyEventL( aKeyEvent, aType );
			TInt cur2 = CurrentItem();
			if(iTextArray->At(cur2).iImageIndex==0)
				iThumbTimer->Start(2000000,2000000,TCallBack(CSmViewerContainer::CreateThumb,this));
			return key;


       }// iTextArray->Count()>0

	return EKeyWasNotConsumed;

}



TInt CSmViewerContainer::CreateThumb(TAny* aThis)
{
	((CSmViewerContainer*)aThis)->iThumbTimer->Cancel();
	((CSmViewerContainer*)aThis)->CreateThumb();
	//The value returned by the callback function. A true (non-zero) value indicates that the callback function should be called again. A false (zero) value indicates that the callback function should not be called again. This is particularly important when the callback is used with the CIdle and CPeriodic classes 
	return EFalse;
}

void CSmViewerContainer::CreateThumb()
{

	if(iAppUi->logfile) iAppUi->logfile->Log(_L8("CreateThumb"));

	TImageInfo* info = &((*iTextArray)[CurrentItem()]);

	CPAlbImageUtil* util = CPAlbImageUtil::NewL();
	util->SetCreateThumbnails(ETrue);
	TFullName name;
	name.Copy(info->iFileName.FullName());
	name.LowerCase();


	iAdderBuf = name;

	if(iAppUi->logfile) iAppUi->logfile->Log(name);


	TBool aLocal = EFalse;

	TInt err = name.Find(_L("\\nokia\\images\\"));
	if((err!=KErrNotFound))
		aLocal = ETrue;

	if(iAppUi->logfile) iAppUi->logfile->Log(_L("Local %d"),aLocal);


	if(aLocal)
	{
		iImageData->SetNameAndSubFolderL(name);
		TRAP(err,util->CreateThumbnailL(*iImageData));
	}
	else
	{

		CFileMan* fileman=0;
		fileman = CFileMan::NewL(iCoeEnv->FsSession());
		err = fileman->Copy(name,_L("c:\\nokia\\images\\tempimg0111.jpg"),EFalse);
		delete fileman; 
	}

	if(err==KErrNone)
	{
		if(!aLocal)
			iImageData->SetNameAndSubFolderL(_L("c:\\nokia\\Images\\tempimg0111.jpg"));

#ifdef _DEBUG
		TName nm;
		nm.Copy(iImageData->ImageName());
#endif
		if(!iImageData->ThumbnailExistsL())
			util->CreateThumbnailL(*iImageData);
		
		CFbsBitmap* bmp = 0;		
		if(iImageData->ThumbnailExistsL())
			bmp = util->GetThumbnailL(*iImageData);

		if(bmp!=0)
		{
			CGulIcon* icon =  CGulIcon::NewL(bmp);
			iIcons->AppendL(icon);
			info->iImageIndex = iIcons->Count()-1;
			CreateText(*info);

			if(!aLocal)
			{
				HBufC* buf = iImageData->ResolveImagePathL();
				iCoeEnv->FsSession().Delete(buf->Des());
				delete buf;
				buf = iImageData->ResolveThumbnailPathL();
				iCoeEnv->FsSession().Delete(buf->Des());
				delete buf;
			}

			DrawNow();
		}

	}

	delete util;
}




TInt CSmViewerContainer::AddItem(TAny* aPtr)
{
	if(aPtr)
		((CSmViewerContainer*)aPtr)->AddItem();
	return 0;
}


void CSmViewerContainer::CancelThreadForFindImages()
{

	iSplashWindow=EFalse;
//	SizeChanged();
	iAdderBuf = _L("");

	if(iAdder)
		if(iAdder->IsActive())
		{
			iAdder->Cancel();
		}

	if(iAdder)
		if(iFactory)
			iFactory->CancelAsyncGet();
	
	delete iAdder;
	iAdder = 0;
	delete iFactory;
	iFactory = 0;

	RThread thread ;
	TInt error = thread.Open(KTxtShared);
	if(error == KErrNone)
	{

//	    CMsvOperationWait* wait = CMsvOperationWait::NewLC();
//	    wait->Start();
		iThreadCancel = ETrue;
		thread.Logon(iThreadRQ) ;

		TRequestStatus* st = &iAdderProgress;
		thread.RequestComplete(st,KErrGeneral);
		
		thread.Close() ;


//		CActiveScheduler::Start();
//		delete wait;
		User::WaitForRequest(iThreadRQ);
	}


//		User::WaitForRequest(iAdderProgress);
}


void CSmViewerContainer::StartThreadForFindImages()
{


	iThreadCancel = EFalse;
	
	RThread thread;

	TInt error = 0;
	TThreadFunction funct(CSmViewerContainer::StartFindImage);
	error = thread.Create(KTxtShared,
            funct,
            KDefaultStackSize*5,
            NULL,
            (TAny*)this);

  	User::LeaveIfError(error) ;

	// log on to thread -	sets iStatus to KRequestPending 
	//						requests notification of thread completion
	iThreadRQ = 0;
	iThreadCancel = EFalse;
	// give thread low priority 
	thread.SetPriority(EPriorityMuchLess) ;
	// resume thread (wake it up sometime after this function returns)

//	StopWaiter();
	thread.Resume() ;
	thread.Close() ;


//	iAppUi->StartWaiter();

}



/*
void CSmViewerContainer::RunL()
{
	StopWaiter();
}


void CSmViewerContainer::DoCancel()
{
	StopWaiter();
}
*/

TInt CSmViewerContainer::StartFindImage(TAny* aThis)
{
	((CSmViewerContainer*)aThis)->StartFindImage();
	return KErrNone;
}


void CSmViewerContainer::StartFindImage()
{


	CTrapCleanup* clenup = 0;
	if ((clenup = CTrapCleanup::New())==NULL)
		User::Leave(KErrGeneral);


	RFs fs;
	User::LeaveIfError(fs.Connect());

	TRAPD(err1,{

	logfile2=0;
	{
//		logfile2 = CLogFile::NewL(_L("SMVLog2.txt"), ETrue);
		if(logfile2)
		{
			logfile2->SetAutoNewline(ETrue);
			logfile2->SetAutoTimeStamp(EFalse);
			logfile2->SetAutoFlush(ETrue);
			logfile2->PrintCurrentTime();
			logfile2->Log(_L8("Finder start"));
		}
	}

	});
	
	iCatalogLevel = 0;

	TRAPD(err,{
		TRAPD(err1,
			ForAllMatchingDrives(fs); 
			);
		fs.Close();
	});


	iAdderBuf = _L("_XXX_STOP");
	if(iAdder)
		iAdder->CallBack();

	delete clenup;
	delete logfile2;
	logfile2=0;

	
}


void CSmViewerContainer::ForAllMatchingDrives(RFs& aSession)
{
	ForAllMatchingFiles(aSession,KRootFolder); 
	TInt err;
	err=iCoeEnv->FsSession().ScanDrive(_L("E:"));
	if((KErrNone == err)||(KErrInUse == err))
		ForAllMatchingFiles(aSession,KRootFolder2); 
}
	
void CSmViewerContainer::ForAllMatchingFiles(RFs& aSession, const TDesC& aScanDir)
{
	iCatalogLevel++;

		TimeWait(1000);

		if(iThreadCancel)
			return;

		TFindFile file_finder(aSession);

        CDir *file_list = 0; 
        TInt err = 0;

		for(TInt i=0;i<7;i++)
		{

			TName wild = _L("none");
			switch(i)
			{
				case 0:
					wild = KWildNameJPG;
				break;
				case 1:
					wild = KWildNameOTA;
				break;
				case 2:
					wild = KWildNameGIF;
				break;
				case 3:
					wild = KWildNamePNG;
				break;
				case 4:
					wild = KWildNameTIFF;
				break;
				case 5:
					wild = KWildNameWBMP;
				break;
				case 6:
					wild = KWildNameWMF;
				break;
				default:
				break;
			}



			

			if(iThreadCancel)
				break;
			file_list = 0; 
			err = file_finder.FindWildByDir(wild,aScanDir, file_list);
			if(err==KErrNone)
			{
				
				TInt i;
				for (i=0; i<file_list->Count(); i++)
				{
					if(iThreadCancel)
						break;
					TEntry entry = (*file_list)[i];
					//    			if(entry.iSize!=2468)// anti thumb
					{
						iAdderBuf = aScanDir;
						iAdderBuf.Append(entry.iName);

						if(!(aSession.IsValidName(iAdderBuf)))
							continue;




						if(iAdder)
						{


							TTime time;
							TInt error=0;
							error = aSession.Modified(iAdderBuf,time);


							if(logfile2)
							{
								logfile2->Log(iAdderBuf);
								logfile2->Log(_L("attrib 0x%X"),entry.iAtt);
								logfile2->Log(_L("typeValid 0x%X"),entry.IsTypeValid());
								logfile2->Log(_L("size %d"),entry.iSize);
								logfile2->Log(_L("error %d"),error);
								TBuf<100> dateString;
								_LIT(KDateString,"%-B%:0%J%:1%T%:2%S%:3%");
								time.FormatL(dateString,KDateString);
								logfile2->Log(dateString);
							}


							if(error!=KErrNone)
								continue;
//							if(entry.iAtt>0xFF)
//								continue;


							if(iAdder)
							{
//								TRequestStatus *pS=(&iAdderProgress);
//								User::RequestComplete(pS,0);
								iAdderProgress = KRequestPending;
								iAdder->CallBack();
								//							TimeWait(100000);
								User::WaitForRequest(iAdderProgress);
								
							}
							if(logfile2)
								logfile2->Log(_L8("end of wait"));

						}// if(iAdder)
					}
				}// for (i=0; i<file_list->Count(); i++)
				
			}
			
			delete file_list;
		}

 		TFindFile dir_finder(aSession);
        CDir* dirs_list=0; 
    	err = dir_finder.FindWildByDir(_L("*"),aScanDir, dirs_list);
        if(err==KErrNone)
            {
            TInt i;
            for (i=0; i<dirs_list->Count(); i++)
            {
				if(iThreadCancel)
					break;
    			if((*dirs_list)[i].IsDir())
    			{
				    TParse fullentry;
					fullentry.Set(aScanDir,NULL,NULL); // 5,6,7
					fullentry.AddDir((*dirs_list)[i].iName);
					TInt err = fullentry.FullName().Find(_L("Images_tn"));
					TInt err1 = fullentry.FullName().Find(_L("PAlbTN"));
					if((err!=KErrNotFound)||(err1!=KErrNotFound))
						continue;

					if(iCatalogLevel==2)
					{
						iPercent++;
						DrawNow();
						iAdderBuf = _L("_DRAW_NOW");
						if(iAdder)
							iAdder->CallBack();
						TimeWait(10000);
					}
    				ForAllMatchingFiles(aSession,fullentry.FullName());
    			}
            }
            }
    
        delete dirs_list;

	iCatalogLevel--;
    
}



void CSmViewerContainer::TimeWait(TInt aInterval)
{
	TTimeIntervalMicroSeconds32 timeIntervalSeconds(aInterval);
	TRequestStatus timerStatus=0;
	RTimer timer; 
	timer.CreateLocal(); 
	// issue and wait
	timer.After(timerStatus,timeIntervalSeconds);
	User::WaitForRequest(timerStatus);
}


void CSmViewerContainer::AddItem()
{

	if(iAppUi->logfile)
		iAppUi->logfile->Log(iAdderBuf);
	if(iAdderBuf==_L("_XXX_STOP"))
	{
//		iAppUi->StopWaiter();
		CancelThreadForFindImages();
		SizeChanged();

		CEikButtonGroupContainer * pCbaGroup = CEikButtonGroupContainer::Current();
		pCbaGroup->SetCommandSetL(R_AVKON_SOFTKEYS_OPTIONS_EXIT);
		pCbaGroup->DrawNow();
		SetCaptionL();

		Sorting();

		if(iTextArray->Count()>0)
		{
			TInt cur2 = CurrentItem();
			if(iTextArray->At(cur2).iImageIndex==0)
				if(iThumbTimer)
					iThumbTimer->Start(2000000,2000000,TCallBack(CSmViewerContainer::CreateThumb,this));
		}

		DrawNow();


		return;
	}

	
	if(iAdderBuf==_L("_DRAW_NOW"))
	{
		DrawNow();
		return;
	}



//	if(iAdderProgress==KRequestPending)
//		User::WaitForRequest(iAdderProgress);
	
/*
   CPAlbImageData* data = CPAlbImageData::NewL();
   CleanupStack::PushL( data );
   TBool result( CPAlbImageFetchPopupList::RunL( data ) );
   CleanupStack::PopAndDestroy(); // data
*/

/*
	CPAlbImageUtil* util = CPAlbImageUtil::NewL();
	util->SetCreateThumbnails(ETrue);
	TRAPD(err,util->CopyImageL(_L("c:\\nokia\\Images\\Image.jpg"),EFalse));

	if(err==KErrNone)
	{
		CPAlbImageData* data = CPAlbImageData::NewL(*(util->ImageData()));
		if(!data->ThumbnailExistsL())
			util->CreateThumbnailL(*data);
		
		if(data->ThumbnailExistsL())
			bmp = util->GetThumbnailL(*data);
	}
*/

//	iImageData->SetNameAndSubFolderL(_L("c:\\nokia\\Images\\Image.jpg"));

	
	iImageData->SetNameAndSubFolderL(iAdderBuf);
	iCurentName.Set(iAdderBuf,NULL,NULL);

	iCurTel = 0;
//	TName  nameuesr;


	if(iAppUi->logfile) iAppUi->logfile->Log(_L8("GetPhoneByPath"));

	TRAPD(err, {
	CListContactsArray* cntArray = new (ELeave) CListContactsArray(5);
	iAppUi->DbTools()->GetPhoneByPathL(iAdderBuf,*cntArray);
	iCurTel = cntArray->Count();
	delete cntArray;
	}
	);

	if(iAppUi->logfile) iAppUi->logfile->Log(_L("GetPhoneByPath2 %d"),err);



	if(iFactory&&iImageData)
	{
		iFactory->CancelAsyncGet();
		if(iAppUi->logfile) iAppUi->logfile->Log(_L8("GetThumbnailAsync"));
		TInt err = iFactory->GetThumbnailAsync(*iImageData);
		if(iAppUi->logfile) iAppUi->logfile->Log(_L("GetThumbnailAsync %d "),err);

		{// добавление картинки типа без nhumbail
//			TImageInfo info;
//			info.iFileName = iCurentName;
//			info.iImageIndex =0;
//			if(iCurTel>0)
//				info.iLinked = ETrue;
//			CreateText(info);
//			iTextArray->AppendL(info);
//			iListBox->HandleItemAdditionL();
//			TRequestStatus* st = &iAdderProgress;
//			User::RequestComplete(st,KErrNone);
//			iAdderProgress =0;
		}

	}
	else
	{// аварийный выход 

		RThread thread ;
		TInt error = thread.Open(KTxtShared);
		if(error == KErrNone)
		{
			TRequestStatus* st = &iAdderProgress;
			thread.RequestComplete(st,KErrGeneral);
	//		iAdderProgress=0;
			thread.Close() ;
		}

	}

}





/*

	CPAlbImageUtil* util = CPAlbImageUtil::NewL();
	util->FetchFreeImageNameL(_L("Image.jpg"));
	CFbsBitmap* bmp = 0;
	CPAlbImageData* data = CPAlbImageData::NewL(*(util->ImageData()));
	util->SynchronizeL(*data);
	if(!data->ThumbnailExistsL())
	{
		util->CreateThumbnailL(*data);
	}
	bmp = util->GetThumbnailL(*data);
*/

//	delete factory;
	//delete data;
//	delete bmp;
//	delete util;	 
	//"1\tNew"
	



void CSmViewerContainer::GetSecondString(const TDesC& aFileName,TDes& aString)
{
	
	TTime time;

	if(iCoeEnv->FsSession().IsValidName(aFileName))
	{
		iCoeEnv->FsSession().Modified(aFileName,time);
		_LIT(KDateString2,"%*E%*D%X%*N%*Y %1 %2 '%3");
		time.FormatL(aString,KDateString2);
	}
	else
		aString = _L("File not found");


}

void CSmViewerContainer::CreateText(TImageInfo& aInfo)
{
		TBuf<255> string;


//0	EMbmSmviewerNoimage
//1 EMbmSmviewerTellink
//2 EMbmSmviewerNotellink
//3	EMbmSmviewerMarked_add

		if(!iGrid)
		{
			string.Format(_L("%d"),aInfo.iImageIndex);
			string.Append(_L("\t"));
			string.Append(aInfo.iFileName.Name());
			string.Append(_L("\t"));
			TName second;
			GetSecondString(aInfo.iFileName.FullName(),second);
			string.Append(second);
			if(aInfo.iSelected != EFalse)
				string.Append(_L("\t3"));
			else
				if(aInfo.iLinked != EFalse)
					string.Append((_L("\t1")));
				else
					string.Append((_L("\t2")));
		}
		else
		{
			string.Format(_L("%d\t"),aInfo.iImageIndex);
			if(aInfo.iSelected != EFalse)
				string.Append(_L("3"));
			else
				if(aInfo.iLinked != EFalse)
					string.Append((_L("1")));
				else
					string.Append((_L("2")));
		}

		aInfo.iText = string;
}

void CSmViewerContainer::MPTfoCreateComplete( CPAlbImageFactory* /*aObj*/, TInt aError, CFbsBitmap* aBitmap )
{
	if(aError==KErrNone && aBitmap!=0)
	{
		CGulIcon* icon =  CGulIcon::NewL(aBitmap);
		iIcons->AppendL(icon);

		TImageInfo info;
		info.iFileName = iCurentName;
		info.iImageName = iCurentName.Name();
		info.iImageIndex = iIcons->Count()-1;

		if(iCoeEnv->FsSession().IsValidName(iCurentName.FullName()))
			iCoeEnv->FsSession().Modified(iCurentName.FullName(),info.iTime);
				
		if(iCurTel>0)
			info.iLinked = ETrue;
		CreateText(info);

		iTextArray->AppendL(info);

		SetCaptionL();
//		iListBox->HandleItemAdditionL();

	}
	else
	{
			TImageInfo info;
			info.iFileName = iCurentName;
			info.iImageIndex =0;
			info.iImageName = iCurentName.Name();
			if(iCurTel>0)
				info.iLinked = ETrue;
		
			if(iCoeEnv->FsSession().IsValidName(iCurentName.FullName()))
				iCoeEnv->FsSession().Modified(iCurentName.FullName(),info.iTime);


			CreateText(info);
			iTextArray->AppendL(info);
	}




	RThread thread ;
	TInt error = thread.Open(KTxtShared);
	if(error == KErrNone)
	{
		TRequestStatus* st = &iAdderProgress;
		thread.RequestComplete(st,aError);
//		iAdderProgress=0;
		thread.Close() ;
	}

}


void CSmViewerContainer::SetGraphicIconL()
{
    CleanupStack::PushL( iIcons );

	if(!iGrid)
		((CAknDoubleLargeStyleListBox*)iListBox)->ItemDrawer()->ColumnData()->SetIconArray(iIcons);
	else// CAknGMSStyleGrid
		((CAknGMSStyleGrid*)iListBox)->ItemDrawer()->FormattedCellData()->SetIconArrayL(iIcons);
    CleanupStack::Pop();

	iAppUi->iIcons = iIcons;
}


void CSmViewerContainer::AttachL(TInt index)
{
	delete iSaver;

	if(iTextArray->At(index).iImageIndex!=0)
	{
//#ifdef _DEBUG
//			TContactInfo info = iCont->iTextArray->At(iCurrentThumbail);
//#endif

		TImageInfo& inf = ((*(iTextArray))[index]);
		HBufC8* Thumbail=NULL;
		CAknIconArray* icons = iAppUi->View1IconsArray();

		const TSize size = icons->At(inf.iImageIndex)->Bitmap()->SizeInPixels();
		if (size.iWidth>0 && size.iHeight>0)
		{
			TInt descSize = (size.iWidth * size.iHeight * KBytesPerPixel) + KHeaderSize;   //why these magic numbers????
//					TInt descSize = 200000; // for testing
			TRAPD(err, Thumbail = HBufC8::NewL(descSize));
			iSaver = new (ELeave) CSmViewerThunbSaver(iAppUi,icons->At(inf.iImageIndex)->Bitmap(),Thumbail,this);
			iSaver->Start();
		}
	}
	else
		AttachSecondPhaseL();


	
}

void CSmViewerContainer::AttachSecondPhaseL()
{

	if(iRenamedArray)
	{
		for(TInt i=0;i<iRenamedArray->Count();i++)
		{
			(*iRenamedArray)[i].iThumbail = HBufC8::NewL(iSaver->iBuffer->Length());
			(*((*iRenamedArray)[i].iThumbail)) = iSaver->iBuffer->Des();
			(*iRenamedArray)[i].iLinked = ETrue;
			iAppUi->DbTools()->UpdateContactL((*iRenamedArray)[i]);
		}
	}


	delete iSaver->iBuffer;
	iSaver->iBuffer=0;
	delete iRenamedArray;
	iRenamedArray = 0;

}


// Destructor
CSmViewerContainer::~CSmViewerContainer()
{


	delete iRenamedArray;

	delete iSaver;

	CancelThreadForFindImages();


	delete iImage;
	if(iThumbTimer)
		iThumbTimer->Cancel();
	delete iThumbTimer;
	iThumbTimer=0;
	delete iNaviDecorator;

	if(iAdder)
		iAdder->Cancel();
	delete iAdder;
	iAdder =0;
	delete iImageData;
	iImageData  =0;
	delete iFactory;
	iFactory = 0;
//    delete iIcons;
//	iIcons = 0;
//	delete iTextArray;
//	iTextArray = 0;
	if(!iGrid)
		delete ((CAknDoubleLargeStyleListBox*)iListBox);
	else// CAknGMSStyleGrid
		delete ((CAknGMSStyleGrid*)iListBox);

	iListBox = 0;
}

// ---------------------------------------------------------
// CSmViewerContainer::SizeChanged()
// Called by framework when the view size is changed
// ---------------------------------------------------------
//
void CSmViewerContainer::SizeChanged()
{
	TRect all = Rect();

	if(iImage&&iListBox)
	if(!iSplashWindow)
	{
		if(iGrid)
        {
//	        ((CAknGMSStyleGrid*)iListBox)->SetItemHeightL(48);
//			((CAknGMSStyleGrid*)iListBox)->SizeChanged();
	        AknLayoutUtils::LayoutControl(((CAknGMSStyleGrid*)iListBox), Rect(), KColorIndex,
            KLeftOfGrid, KTopOfGrid, ELayoutEmpty, ELayoutEmpty,KWidthOfGrid, KHightOfGrid);
        }

		else
			iListBox->SetExtent( TPoint(0,0), all.Size());
		iImage->SetExtent( TPoint(0,0), TSize(0,0));
	}
	else
	{
		TSize bmps = iImage->Bitmap()->SizeInPixels();
		iImage->SetExtent(TPoint((all.Size().iWidth - bmps.iWidth)/2,(all.Size().iHeight - bmps.iHeight)/2),bmps);
		iListBox->SetExtent( TPoint(0,0), TSize(0,0));
	}
}

// ---------------------------------------------------------
// CSmViewerContainer::CountComponentControls() const
// ---------------------------------------------------------
//
TInt CSmViewerContainer::CountComponentControls() const
    {
    return 2; // return nbr of controls inside this container
    }

// ---------------------------------------------------------
// CSmViewerContainer::ComponentControl(TInt aIndex) const
// ---------------------------------------------------------
//
CCoeControl* CSmViewerContainer::ComponentControl(TInt aIndex) const
    {
    switch ( aIndex )
        {
        case 0:
            return iListBox;
        case 1:
            return iImage;
        default:
            return NULL;
        }
    }

// ---------------------------------------------------------
// CSmViewerContainer::Draw(const TRect& aRect) const
// ---------------------------------------------------------
//
void CSmViewerContainer::Draw(const TRect& aRect) const
{

	
	CWindowGc& gc = SystemGc();
	gc.SetPenStyle(CGraphicsContext::ENullPen);
	gc.SetBrushColor(KRgbWhite);
	gc.SetBrushStyle(CGraphicsContext::ESolidBrush);
	gc.DrawRect(aRect);

	if(iSplashWindow)
	{
		DrawProgress();
	}
}

void CSmViewerContainer::DrawProgress() const
{
	    TRect rect = TRect( TPoint(iImage->Rect().iTl.iX,iImage->Rect().iBr.iY+4),TSize(iImage->Rect().Size().iWidth,5));

		CWindowGc& gc = SystemGc();
		gc.SetPenStyle(CGraphicsContext::ESolidPen);
		gc.SetBrushColor(TRgb(0xaa0000));
		gc.SetPenColor(TRgb(0xaa0000));
		gc.SetBrushStyle(CGraphicsContext::ENullBrush);
		gc.DrawRect(rect);
		gc.SetBrushStyle(CGraphicsContext::ESolidBrush);
		gc.DrawRect(TRect(rect.iTl,TSize((rect.Size().iWidth)*iPercent/iCatalogCounter,rect.Size().iHeight)));
}


void CSmViewerContainer::DeleteAllSelected()
{
	for(TInt i=0;i<iTextArray->Count();i++)
	{
#ifdef _DEBUG
		TFullName trf = iTextArray->At(i).iFileName.FullName();
#endif
		if(iTextArray->At(i).iSelected!=EFalse)
		{
			TInt err = DeleteItem(i);
			if(err==KErrNone)
			{
				iTextArray->Delete(i,1);
				i--;
			}
			else
				return;
		}
	}
}

void CSmViewerContainer::DeleteCurrent()
{
	TInt err = DeleteItem(CurrentItem());
	if(err==KErrNone)
	{
		iTextArray->Delete(CurrentItem(),1);
	}
}

TInt CSmViewerContainer::DeleteItem(TInt aIndex)
{
	if(aIndex>iTextArray->Count())
		return KErrNotFound;

	const TDesC& fulln = iTextArray->At(aIndex).iFileName.FullName();
	HBufC* buf;

	if(iImageData!=0)
	{
		iImageData->SetNameAndSubFolderL(fulln);
		buf = iImageData->ResolveThumbnailPathLC();
		if(buf!=0)
			(iEikonEnv->FsSession()).Delete(buf->Des());
		CleanupStack::PopAndDestroy(buf);


	}

	DeleteItemFromContacts(fulln);


	return iCoeEnv->FsSession().Delete(fulln);
}


void CSmViewerContainer::DeleteItemFromContacts(const TDesC& aFullName)
{
	TInt iCurTel=0;
	CListContactsArray* cntArray=0;
	TRAPD(err, {
	cntArray = new (ELeave) CListContactsArray(5);
	iAppUi->DbTools()->GetPhoneByPathL(aFullName,*cntArray);
	iCurTel = cntArray->Count();
	});

	if(iCurTel>0)
	{
		while(iCurTel--)
		{
			TContactInfo inf = cntArray->At(iCurTel);
			inf.iAltImageIndex=0;
			inf.iImageIndex=0;
			iAppUi->DbTools()->UpdateContactL(inf);
		}
	}

	delete cntArray;
}

void CSmViewerContainer::Rename()
{
	TInt cur = CurrentItem();
	TParse filename(iTextArray->At(cur).iFileName);
	TName name(filename.Name());


	
	CAknTextQueryDialog* dlg;
    dlg = CAknTextQueryDialog::NewL(name);
	TBool answer = dlg->ExecuteLD( R_RENAME_QUERYDLG );
	if(answer)
	{

		
		TFullName newname = filename.DriveAndPath();
		newname.Append(name);
		newname.Append(filename.Ext());


		TFileName fname(filename.FullName());
		fname.LowerCase();
		TFileName fname2(newname);
		fname2.LowerCase();
		TInt res = fname2.Find(fname);
		if(res!=KErrNotFound)
			return;


		TTime time;
		TInt error=0;
		TBool rename(ETrue);
		error = iEikonEnv->FsSession().Modified(newname,time);
		if(error==KErrNone)
			if(!(iAppUi->ShowConfirmation(ERenameExistFile)))
				rename = EFalse;
			else
				if(iCoeEnv->FsSession().Delete(newname)!=KErrNone)
					rename = EFalse;
				else
				{// если такой файл уже есть то старую строку нужно удалить для перезаписи

					TInt ind = Index(fname2);
					if(ind!=KErrNotFound)
					{
						iTextArray->Delete(ind);
						if(ind<cur)
							cur--;

					}

				}




		if(rename)
			if((error = iCoeEnv->FsSession().Rename(filename.FullName(),newname))==KErrNone)
			{
				// проапдейтим строчку 
				(*iTextArray)[cur].iFileName.Set(newname,0,0);
				(*iTextArray)[cur].iImageName = (*iTextArray)[cur].iFileName.Name();
				CreateText((*iTextArray)[cur]);
				iListBox->SetCurrentItemIndex(cur);


				// проапдейтим телефонный номер

				delete iRenamedArray ;
				iRenamedArray=0;
				TRAPD(err, {
				iRenamedArray = new (ELeave) CListContactsArray(5);
				iAppUi->DbTools()->GetPhoneByPathL(filename.FullName(),*iRenamedArray);
				}
				);

				if(iRenamedArray)
					for(TInt i=0;i<iRenamedArray->Count();i++)
					{
						(*iRenamedArray)[i].iFileName.Set(newname,0,0);
					}


				DeleteItemFromContacts(filename.FullName());
				AttachL(cur);

				// проапдейтим сортировку
				Sorting();

				SetCaptionL();

				TInt ind2 = Index(newname);
				if(ind2!=KErrNotFound)
				{
					iListBox->SetCurrentItemIndex(ind2);
				}


				DrawNow();

			}

	}

}

void CSmViewerContainer::SetCaptionL(TBool aNavi/* =ETrue */)
{

	TBuf<128> caption;
	iEikonEnv->ReadResource(caption,R_PROGRAMMNAME);

	CAknTitlePane* TitlePane = 0;
	TitlePane = STATIC_CAST(CAknTitlePane*,iAppUi->StatusPane()->ControlL(TUid::Uid(EEikStatusPaneUidTitle)));
	TitlePane->SetTextL(caption);


	TBuf<128> format;
	TBuf<128> caption2;

	if(iSplashWindow)
	{
		iEikonEnv->ReadResource(format,R_VIEW1_CAPTION_STRING_FORMAT2);
		caption2.Format(format,iTextArray->Count());
	}
	else
	{
		iEikonEnv->ReadResource(format,R_VIEW1_CAPTION_STRING_FORMAT);
		caption2.Format(format,iTextArray->Count(),SelectedItems());
	}

	if(aNavi)
	{
		delete iNaviDecorator;
		CAknNavigationControlContainer* iNaviPane;
		CEikStatusPane *sp = ((CAknAppUi*)iEikonEnv->EikAppUi())->StatusPane();
		iNaviPane = (CAknNavigationControlContainer*)sp->ControlL(TUid::Uid(EEikStatusPaneUidNavi));
		iNaviDecorator = iNaviPane->CreateNavigationLabelL(caption2);
		iNaviPane->PushL(*iNaviDecorator);
	}

}

// End of File  


CListImagesArray::CListImagesArray(TInt aGranularity) 
: CArrayFixFlat<TImageInfo>(aGranularity)
{
	
}


EXPORT_C TInt  CListImagesArray::MdcaCount() const
{
	return 	Count();
}


EXPORT_C TPtrC16 CListImagesArray::MdcaPoint(TInt aIndex) const
{
	return At(aIndex).iText;
}

CCoeEnv* CSmViewerContainer::Environment()
{
	return iCoeEnv;
}


void CSmViewerContainer::UpdateImageNumberL(TContactInfo &aInfo)
{
	aInfo.iImageIndex = 0;
	if(aInfo.iFileName.FullName().Length()!=0)
	for(TInt i=0;i<iTextArray->Count();i++)
	{
		TFileName fname = iTextArray->At(i).iFileName.FullName();
		fname.LowerCase();
		TInt res = aInfo.iFileName.FullName().Find(fname);
		if(res!=KErrNotFound)
		{
			aInfo.iImageIndex = iTextArray->At(i).iImageIndex;
			break;
		}
	}
}

void CSmViewerContainer::UpdateImageLinkL(TInt aImgIndex)
{

	for(TInt i=0;i<iTextArray->Count();i++)
	{
		TInt imageindex = iTextArray->At(i).iImageIndex;
		if(aImgIndex==imageindex)
		{
			{// возможна ситуация что линк на эту картинку остался на других контактах
				CListContactsArray* cntArray = new (ELeave) CListContactsArray(5);
				iAppUi->DbTools()->GetPhoneByPathL(iTextArray->At(i).iFileName.FullName(),*cntArray);
				iCurTel = cntArray->Count();
				delete cntArray;
				if(iCurTel>0)
					iTextArray->At(i).iLinked = ETrue;
				else
					iTextArray->At(i).iLinked = EFalse;
			}
			CreateText(iTextArray->At(i));
			break;
		}
	}
}

TInt CSmViewerContainer::GetFileName(TInt& aInd,TDes& aFNAme)
{
#ifdef _DEBUG
//	TInt count  = iTextArray->Count();
#endif

	TBool cic(iAppUi->Settings().iSlideCyclic==TSmViewerSettings::ESlideCyclicOn);
//	TBool end(EFalse);

	RArray<TInt> array;
	if(iAppUi->ShowSelected())
	{
		for(TInt i=0;i<iTextArray->Count();i++)
			if(iTextArray->At(i).iSelected!=EFalse)
				array.Append(i);
	}
	else
	{
		for(TInt i=0;i<iTextArray->Count();i++)
			array.Append(i);

	}


	if(aInd>=array.Count())
	{
		aInd = 0;
//		end = ETrue;
	}
	if(aInd<0)
	{
		aInd = array.Count()-1;
//		end = ETrue;
	}

	
	aFNAme.Copy(iTextArray->At(array[aInd]).iFileName.FullName());

//	if((cic==EFalse)&&(end))
	if((cic==EFalse)&&(iStopShowIndex==aInd))
		aFNAme = _L("");


	TInt res = -1;

	if(aFNAme.Length()!=0)
		res = array[aInd];

	array.Reset();
	array.Close();

	return res;

}


void CSmViewerContainer::SetCurrentIten(TInt index)
{
	if(index>=iTextArray->Count())
		return;

	iListBox->SetCurrentItemIndex(index);

}

void CSmViewerContainer::Sorting()
{
	TSmViewerSettings::TListBoxSort sort((TSmViewerSettings::TListBoxSort)(iAppUi->Settings().iListBoxSort));

	if(sort == TSmViewerSettings::EByName)
	{
		TKeyArrayFix actNameKey(_FOFF(TImageInfo,iImageName),ECmpNormal);
		iTextArray->Sort(actNameKey);
	}

	if(sort == TSmViewerSettings::EByDate)
	{
		TKeyArrayFix actTimeKey(_FOFF(TImageInfo,iTime),ECmpTInt64);
		iTextArray->Sort(actTimeKey);
		Reverse();
	}

	if(iListBox)
		iListBox->HandleItemAdditionL();

//	if(iListBox)
//		iListBox->SetCurrentItemIndex(0);

}

void CSmViewerContainer::ActivateOptions()
{
	iAppUi->ShowOptions(iListBox->CurrentItemIndex());
}

void CSmViewerContainer::Reverse()
{
	CListImagesArray* curarr = new (ELeave) CListImagesArray(2);

	TInt count =iTextArray->Count();
	for(TInt i = 0;i<count;i++)
	{
		curarr->AppendL((*iTextArray)[count-i-1]);
	}


	iListBox->Reset();
	if(!iGrid)
		((CAknDoubleLargeStyleListBox*)iListBox)->Model()->SetItemTextArray(curarr);
	else//CAknGMSStyleGrid
		((CAknGMSStyleGrid*)iListBox)->Model()->SetItemTextArray(curarr);

	iTextArray = curarr;


}

TInt CSmViewerContainer::Index(const TDesC &aFullFileName)
{
	TFileName fname2 = aFullFileName;
	fname2.LowerCase();

	for(TInt i=0;i<iTextArray->Count();i++)
	{
		TFileName fname = iTextArray->At(i).iFileName.FullName();
		fname.LowerCase();
		TInt res = fname2.Find(fname);
		if(res!=KErrNotFound)
			break;
	}

	if(i<iTextArray->Count())
		return i;
	else
		return KErrNotFound;
}
