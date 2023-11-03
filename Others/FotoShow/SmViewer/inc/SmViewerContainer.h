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

#ifndef SMVIEWERCONTAINER_H
#define SMVIEWERCONTAINER_H

// INCLUDES
#include <aknlists.h>
#include <akniconarray.h>
#include <palbimageutil.h> 
#include <palbimagefactory.h> 
#include <palbfetch.h> 
#include <aknprogressdialog.h> 
#include <aknwaitdialog.h>


   
#include "SmViewerAppUi.h"
#include "SmViewerContainer3.h"
// FORWARD DECLARATIONS


// CLASS DECLARATION


struct TImageInfo
{
	TParse iFileName;
	TName  iText;
	TBool  iSelected;
	TBool  iLinked;
	TInt iImageIndex;
	TName iImageName;
	TTime iTime;
	TImageInfo::TImageInfo()
	{
		iImageIndex =0;// no image
		iSelected = EFalse;
		iLinked = EFalse;
	}

};

class CListImagesArray : public CArrayFixFlat<TImageInfo>, public MDesC16Array
{
public:
	CListImagesArray(TInt aGranularity);
//		void CopyL(const MDesC16Array& aArray);
//		inline void operator=(const MDesC16Array& aArray);
	// Mixin members
	IMPORT_C TInt MdcaCount() const;
	IMPORT_C TPtrC16 MdcaPoint(TInt aIndex) const;
};

/**
*  CSmViewerContainer  container control class.
*  
*/
class CSmViewerContainer : public CCoeControl, MPAlbImageFactoryObserver, MEikListBoxObserver, MSmViewerThunbSaverObserver
    {
    public: // Constructors and destructor
		CLogFile* logfile2; // for debug
        
		CSmViewerContainer();
        /**
        * EPOC default constructor.
        * @param aRect Frame rectangle for container.
        */
        void ConstructL(const TRect& aRect,CSmViewerAppUi* aAppUi,TSmViewerSettings::TListBoxType aType);

        /**
        * Destructor.
        */
        ~CSmViewerContainer();

    public: // New functions


   /**
	 sort iTextArray according settings
	*/
		void Sorting();
   /**
	 thumb creator
	*/
		static TInt CreateThumb(TAny* aThis);
		void CreateThumb();

    public: // Functions from base classes
   /**
	 reverse iTextArray item order
	*/
	    void Reverse();
   /**
	 show options view
	*/	    
		void ActivateOptions();
	    void SetCurrentIten(TInt index);
   /**
	 update image number
	*/	    
	    void UpdateImageNumberL(TContactInfo &aInfo);
   /**
	 update image path
	*/	    
		void UpdateImageLinkL(TInt aImgIndex);
	    CCoeEnv* Environment();
   /**
	 set list or grid view type
	*/	    
		void SetListView(TBool aLIst);

   /**
	 return file name
	 \param TInt& aInd, - index from array
	 \param TDes& aFNAme - descriptor for filling
	*/	    
	TInt GetFileName(TInt& aInd,TDes& aFNAme);
	inline TInt MaxName()
		{ return iTextArray->Count(); }

	// observer
	void MPTfoCreateComplete( CPAlbImageFactory* aObj, TInt aError, CFbsBitmap* aBitmap );
   /**
	 create and adjust scroll bar
	*/	    
	void CreateScrollbarL();
   /**
	 update and adjust scroll bar
	*/	    
	void UpdateScrollBar();
   /**
	 start show 
	*/	    
	void ActivateItem(TBool aRunSlide,TBool aSelected=EFalse);
   /**
	 start contacts view
	*/	    
	void ActivateItemLink();
   /**
	 select items
	*/	    
	void SelectItem(TInt aIndex,TBool aSelect);
//	void SelectItemLink(TInt aIndex);
   /**
	 select all items
	*/	    
	void SelectAllItem(TBool aSelect);
   /**
	 select items from memory card
	*/	    
	void SelectMCItem();
   /**
	 select items from main memory (c drive)
	*/	    
	void SelectPHItem();
//	void SelectAllItemLinks();
   /**
	 delete items
	*/	    
	void Delete();
   /**
	 rename items
	*/	    
	void Rename();
   /**
	 text array creator (according type of listbox)
	*/	    
	void CreateText(TImageInfo& aInfo);
   /**
	 delete all selected images
	*/	    
	void DeleteAllSelected();
   /**
	 delete current item
	*/	    
	void DeleteCurrent();
   /**
	 get number selected items
	*/	    
	TInt SelectedItems();
   /**
	 delete item by index
	*/	    
	TInt DeleteItem(TInt aIndex);
   /**
	 delete item by name
	*/	    
	void DeleteItemFromContacts(const TDesC& aFullName);
   /**
	 set caption
	 \param TBool aNavi=ETrue - fill navi bar
	*/	    
	void SetCaptionL(TBool aNavi=ETrue);
   /**
	 count of items
	*/	    
	TInt Count()
		{ return iTextArray->Count(); }
   /**
	 current item
	*/	    
	inline TInt CurrentItem()
	{ if(iListBox!=0) 
			return iListBox->CurrentItemIndex();
		else 
			return -1;	} 
   /**
	 is or not current selected
	*/	    
	TBool IsCurrentItemSelected();
   /**
	 is or not selected
	*/	    
	TBool IsSelectedItems();
   /**
	 grid or list list box
	*/	    
	inline TBool IsGridBox()
	{ return iGrid;	}
   /**
	 start thread for image seeking
	*/	    
	void StartThreadForFindImages();
   /**
	 cancel thread for image seeking
	*/	    
	void CancelThreadForFindImages();
   /**
	 start image seeking
	*/	    
	static TInt StartFindImage(TAny* aThis);
	void StartFindImage();
   /**
	 key processor
	*/	    
	TKeyResponse  OfferKeyEventL(const TKeyEvent& aKeyEvent,TEventCode aType );
   /**
	 add item
	*/	    
	void AddItem();
	static TInt AddItem(TAny* aPtr);
   /**
	 second string from text array filling
	*/	    
	void GetSecondString(const TDesC& aFileName,TDes& aString);
   /**
	 is search in progress
	*/	    
	inline TBool IsInProgress()
	{
		return iSplashWindow;
	}
   /**
	 pointer on icon array
	*/	    
	inline CAknIconArray*  Icons()
		{
			return iIcons;
		}


//	void DoCancel();
//	void RunL();
	    void SizeChanged();
	
    private: // Functions from base classes

       /**
        * From CoeControl,SizeChanged.
        */
		void SetGraphicIconL();
		void ForAllMatchingFiles(RFs& aSession, const TDesC& aScanDir);
		void ForAllMatchingDrives(RFs& aSession);
		void CatalogsCounter(RFs& aSession, const TDesC& aScanDir);

       /**
        * From CoeControl,CountComponentControls.
        */
        TInt CountComponentControls() const;
		void HandleListBoxEventL(CEikListBox* aListBox, TListBoxEvent aEventType);
       /**
        * From CCoeControl,ComponentControl.
        */
        CCoeControl* ComponentControl(TInt aIndex) const;

       /**
        * From CCoeControl,Draw.
        */
        void Draw(const TRect& aRect) const;
		void DrawProgress() const;

       /**
        * From ?base_class ?member_description
        */
        // event handling section
        // e.g Listbox events
		static void TimeWait(TInt aInterval);

	protected:
		void ConstructListBoxL();
		void AttachSecondPhaseL();
		void AttachL(TInt index);


    private: //data

        
		CListContactsArray* iRenamedArray;
        //CAknDoubleLargeStyleListBox* iListBox;    
		//CAknGMSStyleGrid* iListBox;
		CEikListBox* iListBox;
		TBool iGrid;

		CAknIconArray* iIcons;
		CListImagesArray*				 iTextArray;
		CPAlbImageFactory* iFactory;
		CPAlbImageData* iImageData;
		TRequestStatus iStatus;

		TParse iCurentName;

		CSmViewerAppUi* iAppUi;
		CAsyncCallBack* iAdder;
		TFullName iAdderBuf;
		TInt iCurTel;


		TRequestStatus iThreadRQ;
		TBool iThreadCancel;
		CAknNavigationDecorator* iNaviDecorator;

		CPeriodic* iThumbTimer;

		TBool iSplashWindow;
		CEikImage* iImage;
		TInt iPercent;
		TInt iCatalogLevel;
		TInt iCatalogCounter;
		TRequestStatus iAdderProgress;

		CSmViewerThunbSaver* iSaver;

	public:
		TInt Index(const TDesC& aFullFileName);
		TInt iStopShowIndex;

};

#endif

// End of File
