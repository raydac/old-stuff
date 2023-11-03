/*
* ============================================================================
*  Name     : CSmViewerAppUi from SmViewerAppUi.h
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

#ifndef SMVIEWERAPPUI_H
#define SMVIEWERAPPUI_H

// INCLUDES
#include <eikapp.h>
#include <eikdoc.h>
#include <e32std.h>
#include <coeccntx.h>
#include <aknviewappui.h>
#include <akntabgrp.h>
#include <aknnavide.h>
#include "PhoneBookTools.h"
#include "e32std.h"
#include "aknprogressdialog.h"
#include "logfile.h"


// FORWARD DECLARATIONS
class CSmViewerContainer;
class CAknIconArray;
class CAknWaitDialog;



// CONSTANTS
//const ?type ?constant_var = ?constant;


// CLASS DECLARATION

		enum TConfirmation 
		{
			EUnknown,
			EDeleteAllSelectedItems,
			EDeleteCurrentItem,
			ERenameExistFile,
			EExit
		};

struct TSmViewerSettings
{

	enum TListBoxSort
		{
		EByName,
		EByDate,
		};

	TInt iListBoxSort;	

	enum TListBoxType
		{
		EGrid,
		EList
		};
	TInt iListBoxType;	

	enum TSlideCyclic
		{
		ESlideCyclicOn,
		ESlideCyclicOff
		};
	TInt iSlideCyclic;	

	enum TSlideDirection
		{
		ESlideListDown,
		ESlideListUp,
		ESlideRandom
		};
	TInt iSlideDirection;	

	
	enum TSlideInterval
		{
		ESlideInterval3=0,
		ESlideInterval5,
		ESlideInterval8
		};
	TInt iSlideInterval;	
};

/**
 Application UI class.
 Provides support for the following features:
 - EIKON control architecture
 - view architecture
 - status pane
*/

class CSmViewerAppUi : public CAknViewAppUi, MProgressDialogCallback
    {
	friend CSmViewerContainer;
    public: // // Constructors and destructor

		CLogFile* logfile;

		void DynInitMenuPaneL(TInt aResourceId,CEikMenuPane* aMenuPane);

        /**
        * EPOC default constructor.
        */      
        void ConstructL();

        /**
        * Destructor.
        */      
        ~CSmViewerAppUi();
        
    public: // New functions

    public: // Functions from base classes
   /**
	 Return pointer on current picture
	*/
	    inline TInt CurrentPicture()
		{ return iCurName; }
   /**
	 Update image from GuiIcon array
	*/
	    void UpdateImageNumber(TContactInfo& aInfo);
   /**
	 Update image path 
	*/
	    void UpdateImageLink(TInt aImgIndex);
   /**
	 Close curent pop up window if is exist
	*/		
		void ClosePopup();
   /**
	 Start show picture
	 \param TInt aNum, - number in text array
	 \param TBool aRunSlide=EFalse - run as slide or as one picture
	 \param TBool aSelected = EFalse - run selected or all 
	*/		
	    void ShowImage(TInt aNum,TBool aRunSlide=EFalse,TBool aSelected = EFalse);
   /**
	 Start show contacts view
	 \param TInt aNum, - number in text array for selected image
	*/		
		void ShowLink(TInt aNum);
   /**
	 Show main view
	*/		
	    void ShowThumbPanel();
   /**
	 Show about dialog
	*/		
		void About();
   /**
	 Stop wait dialog
	*/		
		void DialogDismissedL( TInt aButtonId );
   /**
	 Start wait dialog
	*/		
		void StartWaiter(TBool aWithCancel=ETrue);
   /**
	 Stop wait dialog
	*/		
		void StopWaiter();
   /**
	 Universal confirmation dialog
	 \param TConfirmation aType - type of confirmation dialog
	*/		
		TBool ShowConfirmation(TConfirmation aType);
   /**
	 return current image name
	 \param TDes& aName - place for filling
	*/		
		TInt GetCurImageName(TDes& aName);
   /**
	 return next image name
	 \param TDes& aName - place for filling
	*/
		void GetNextImageName(TDes& aName);
   /**
	 return previously image name
	 \param TDes& aName - place for filling
	*/
		void GetPrevImageName(TDes& aName);
   /**
	 return random image name
	 \param TDes& aName - place for filling
	*/
		void GetRandomImageName(TDes& aName);
   /**
	 return pointer on contact database tool inctance
	*/
		inline CPhoneBookTools* DbTools()
			{	return iPht; }

   /**
	 return flag type of show (selected or all)
	*/
		inline ShowSelected()
		{
			return iShowSelected;
		}
   /**
	 return settings
	*/
		TSmViewerSettings& Settings()
				{ return iSettings;	}

   /**
	 load settings
	*/
		TInt LoadSettings();
   /**
	 save settings
	*/
		TInt SaveSettings();

   /**
	 return pointer on icons array instance
	*/
	CAknIconArray* View1IconsArray()
	{
		return iIcons;
	}

    private:
        // From MEikMenuObserver
		void SetTab(TInt aIndex);
    private:
        /**
        * From CEikAppUi, takes care of command handling.
        * @param aCommand command to be handled
        */
        void HandleCommandL(TInt aCommand);
        /**
        * From CEikAppUi, handles key events.
        * @param aKeyEvent Event to handled.
        * @param aType Type of the key event. 
        * @return Response code (EKeyWasConsumed, EKeyWasNotConsumed). 
        */
        virtual TKeyResponse HandleKeyEventL(
            const TKeyEvent& aKeyEvent,TEventCode aType);

	public:
		TInt iCurName;
    private: //Data
        CAknNavigationControlContainer* iNaviPane;
        CAknTabGroup*                   iTabGroup;
        CAknNavigationDecorator*        iDecoratedTabGroup;
		CPhoneBookTools* iPht;
		CAknIconArray* iIcons;
		CAknWaitDialog* iWaitDlg;
		TSmViewerSettings iSettings;

		TInt64 iRandSed; 

	public:
		void ShowOptions(TInt aCuirIndex);
		void ActivateLastViewL();
		TBool iActivateSlideShow;
		TBool iResetRandom;
		RArray<TInt> iRandAr;

		TUid iLastUid;
		TBool iShowSelected;

		TInt iRealOldCur;
    };

#endif

// End of File
