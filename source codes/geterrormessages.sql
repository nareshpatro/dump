CREATE OR REPLACE PACKAGE XXFND_UI_FUNCTION_SECURITY_PKG AS
/**************************************************************************
REM Copyright (c) 2007 Oracle Corporation. All rights reserved.
REM ***********************************************************************
REM Source Code Control Hdr  : <PVCS Control Header>
REM File name                : XXFND_UI_FUNCTION_SECURITY_PKG.pks
REM Doc Ref(s)               : HA_HCM_Usability_Project_Security_Resp_Function v 03.docx
REM Project                  : Hong Kong Hospital Authority ERP Project 
REM Description: This package contains common security menu functions
REM 
REM Change History Information
REM --------------------------
REM Version  Date         Author           Change Reference / Description 
REM -------  -----------  ---------------  ------------------------------------
REM <1.1>    20-APR-2018  Kayvee Villanueva  Initial Version
REM **************************************************************************/

    FUNCTION XXFND_IS_READ_ONLY_FUNC (user_id IN NUMBER,
                                resp_id IN NUMBER,
                                func_id IN NUMBER)
    RETURN VARCHAR2;
END XXFND_UI_FUNCTION_SECURITY_PKG;
/

CREATE OR REPLACE PACKAGE BODY XXFND_UI_FUNCTION_SECURITY_PKG AS

-- =====================================================================================
--|   Module  :- XXFND_IS_READ_ONLY_FUNC
--|   Description:- A Common function to determine the userâ€™s access to a particular function.
--|
--|   Parameter :- user_id - User logon ID
--|             :- resp_id - Responsibility ID
--|             :- func_id - Function ID
--|   Return :- RETURN VARCHAR2 - Return â€˜Yâ€™ or â€˜Nâ€™ or â€™Fâ€™
--|                               â€˜Yâ€™ means this function is read only.
--|                               â€˜Nâ€™ means this function is not read only.
--|                               â€˜Fâ€™ means the required function not found or 
--|                                   user does not have access to it.
-- ===================================================================================*/
    FUNCTION XXFND_IS_READ_ONLY_FUNC(user_id IN NUMBER,
                               resp_id IN NUMBER,
                               func_id IN NUMBER)
    RETURN VARCHAR2 IS ls_is_read_only VARCHAR2(1);

-- Cursor declaration
    CURSOR C_FUNC_PARA(user_id NUMBER, resp_id NUMBER, func_id NUMBER) IS
        SELECT ff.parameters  
        FROM fnd_user_resp_groups_indirect urgi,
             fnd_responsibility r,
             fnd_compiled_menu_functions cmf,
             fnd_form_functions ff
        WHERE (urgi.responsibility_id, urgi.responsibility_application_id) IN
              (SELECT ifr.responsibility_id,
                      ifr.application_id
               FROM fnd_responsibility ifr
               WHERE ((ifr.version = '4') OR 
                      (ifr.version = 'W') OR
                      (ifr.version = 'M') OR
                      (ifr.version = 'H'))
               ) AND
               (urgi.user_id        = user_id) AND
               (r.responsibility_id = urgi.responsibility_id) AND
               (cmf.menu_id         = r.menu_id) AND
               (ff.function_id      = cmf.function_id) AND 
               (r.responsibility_id = resp_id) AND 
               (ff.function_id      = func_id);

-- Cursor row declaration
    ls_parameter VARCHAR2(2000);

-- Function Body
    BEGIN
        ls_is_read_only := 'F';
        FOR parameter_dtl IN C_FUNC_PARA(user_id, resp_id, func_id)
        LOOP
            ls_parameter := parameter_dtl.parameters;
            FND_FILE.PUT_LINE(FND_FILE.LOG, 'Parameters: ' || ls_parameter);

            IF UPPER(ls_parameter) LIKE UPPER('%QUERY_ONLY="Y"%') THEN
                ls_is_read_only := 'Y';
            ELSE
                ls_is_read_only := 'N';
            END IF;
        END LOOP;

        FND_FILE.PUT_LINE(FND_FILE.LOG, 'READ-ONLY: ' || ls_is_read_only);
        RETURN ls_is_read_only;
    
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
            ls_is_read_only := 'F';
            FND_FILE.PUT_LINE(FND_FILE.LOG, 'No data found.');
        WHEN OTHERS THEN
            ls_is_read_only := 'F';
            FND_FILE.PUT_LINE(FND_FILE.LOG, 'Exception occurred.');

        FND_FILE.PUT_LINE(FND_FILE.LOG, 'READ-ONLY: ' || ls_is_read_only);
        RETURN ls_is_read_only;

    END XXFND_IS_READ_ONLY_FUNC;
END XXFND_UI_FUNCTION_SECURITY_PKG;