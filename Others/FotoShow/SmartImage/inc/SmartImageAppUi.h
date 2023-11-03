/*
* ============================================================================
*  Name     : CSmartImageAppUi from SmartImageAppUi.h
*  Part of  : SmartImage
*  Created  : 06/05/2003 by Oleg Golosovskiy
*  Description:
*     Declares UI class for application.
*  Version  :
*  Copyright: http://www.sw4u.org/
* ============================================================================
*/

#ifndef SMARTIMAGEAPPUI_H
#define SMARTIMAGEAPPUI_H

// INCLUDES
#include <eikapp.h>
#include <eikdoc.h>
#include <e32std.h>
#include <coeccntx.h>
#include <aknviewappui.h>
#include <akntabgrp.h>
#include <aknnavide.h>
#include <cntdb.h>
#include <cntitem.h>
#include <cntfldst.h>
#include "SmartImageAppUi.h"
#include "SmartImage.hrh"
#include "CallInterceptor.h"
#include "ImageConverter.h"


// FORWARD DECLARATIONS
class CSmartImageContainerSettings;
class CSmartImageContainerShow;

// CONSTANTS
//const ?type ?constant_var = ?constant;


// CLASS DECLARATION



struct TSmiSettings
{
/*
		enum TDisplayType
			{
			EInWindow,
			EFullScreen
			};
		TInt iDisplayType;	*/
	
		enum TSmvState
			{
			EActive,
			EInActive
			};
		TInt iState;	

		enum TSmvDetail
			{
			EShow,
			EHide
			};
		TInt iDetailFirstName;	
		TInt iDetailLastName;	
		TInt iDetailPhone;	
		TInt iDetailDuration;	
		TInt iDetailEmail;	
};

/**
* Application UI class.
* Provides support for the following features:
* - EIKON control architecture
* - view architecture
* - status pane
* 
*/
class CSmartImageAppUi : public CAknViewAppUi
    {

public:
	friend CImageConverter;

		struct TLoadInfo
		{
			CImageConverter::TLoadStatus iStatus;
			TName iNumber;
			TName iFirstName;
			TName iLastName;
			TName iEMail;
			TInt  iDuration; 
			TLoadInfo::TLoadInfo()
			{
				iNumber = _L("0");
				iStatus = CImageConverter::EStopLStatus;
				iDuration = 0;
			}
		};

    public: // // Constructors and destructor

        /**
        * EPOC default constructor.
        */      
        void ConstructL();
		void About();
		TInt SaveSettings();
		TInt LoadSettings();

        /**
        * Destructor.
        */      
        ~CSmartImageAppUi();
        
    public: // New functions
		static void TimeWait(TInt aInterval);
		inline CEikonEnv* Environment()
			{ return iEikonEnv; }
		inline TSmiSettings& Settings()
				{ return iSettings;	}
		inline const TSmiSettings& SettingsC() const
				{ return iSettings;	}

		void SendToBackground(TBool aSetAsHidden);
		void SetAsHidden();
		void SendToForeground();
		void ActivateForeground();
		void ShowL(const CLogEvent& aEvent);
		void HandleForegroundEventL(TBool aForeground);
		CSmartImageContainerShow* ShowContainerL();

		void UpdateDuration(TTimeIntervalSeconds aSec);
		
		inline const TLoadInfo& GetLoadInfo() const
			{	return infoAboutCurrentLoadState;	}

		inline CCallInterceptor* Phone()
			{	return iInterceptor;	}

    public: // Functions from base classes

    private:
        // From MEikMenuObserver
        void DynInitMenuPaneL(TInt aResourceId,CEikMenuPane* aMenuPane);

	public:
		void EndOfCall();

        /**
        * From CEikAppUi, takes care of command handling.
        * @param aCommand command to be handled
        */
        void HandleCommandL(TInt aCommand);


    private:

        /**
        * From CEikAppUi, handles key events.
        * @param aKeyEvent Event to handled.
        * @param aType Type of the key event. 
        * @return Response code (EKeyWasConsumed, EKeyWasNotConsumed). 
        */
        virtual TKeyResponse HandleKeyEventL(
            const TKeyEvent& aKeyEvent,TEventCode aType);

    private: //Data

	protected:
		TLoadInfo infoAboutCurrentLoadState;
		CContactItemField* iField;

    private: //tools
		CCallInterceptor* iInterceptor;
		CImageConverter* iConverter;
		TSmiSettings iSettings;
		TInt iFirstLanch;
    };


#endif

// End of File
