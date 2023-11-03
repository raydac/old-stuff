/*
* ============================================================================
*  Name     : CSmViewerView4 from SmViewerView4.h
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

#ifndef __SMVIWERVIEWSETTINGS_H__
#define __SMVIWERVIEWSETTINGS_H__

#include <aknview.h>

// class ContainerClass;

const TUid KView4Id = {4};

class CSmViewerContainerSettings;

/*! 
  @class CSmViwerViewSettings
  
  @discussion An instance of the Application View object for the MultiViews 
  example application
  */
class CSmViwerViewSettings: public CAknView
    {
    public: // Constructors and destructor

        /**
        * EPOC default constructor.
        */
        void ConstructL();

        /**
        * Destructor.
        */
        ~CSmViwerViewSettings();

    public: // Functions from base classes
	    void ShowCurrentL();
        
        /**
        * From ?base_class ?member_description
        */
        TUid Id() const;

        /**
        * From ?base_class ?member_description
        */
        void HandleCommandL(TInt aCommand);

        /**
        * From ?base_class ?member_description
        */
        void HandleClientRectChange();

    private:

        /**
        * From AknView, ?member_description
        */
        void DoActivateL(const TVwsViewId& aPrevViewId,TUid aCustomMessageId,
            const TDesC8& aCustomMessage);

        /**
        * From AknView, ?member_description
        */
        void DoDeactivate();

    private: // Data
        CSmViewerContainerSettings* iContainer;
    };

#endif // __SMVIWERVIEWSETTINGS_H__

