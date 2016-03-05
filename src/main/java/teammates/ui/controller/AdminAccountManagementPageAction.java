package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminAccountManagementPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        new GateKeeper().verifyAdminPrivileges(account);
        
        String instructorGoogleId = this.getRequestParamValue("googleId");
        
        Map<String, ArrayList<InstructorAttributes>> instructorCoursesTable = new HashMap<String, ArrayList<InstructorAttributes>>();
        Map<String, AccountAttributes> instructorAccountsTable = new HashMap<String, AccountAttributes>();
        
        if (instructorGoogleId == null) {
            instructorGoogleId = "";
        }
        List<InstructorAttributes> instructorsList = logic.getInstructorsForGoogleId(instructorGoogleId);
        AccountAttributes instructorAccount = logic.getAccount(instructorGoogleId);
        
        boolean isToShowAll = this.getRequestParamAsBoolean("all");
        boolean isAccountExisting = instructorAccount != null && !instructorsList.isEmpty();
        if (isAccountExisting) {
            instructorAccountsTable.put(instructorAccount.googleId, instructorAccount);
            
            for(InstructorAttributes instructor : instructorsList){
                ArrayList<InstructorAttributes> courseList = instructorCoursesTable.get(instructor.googleId);
                if (courseList == null){
                    courseList = new ArrayList<InstructorAttributes>();
                    instructorCoursesTable.put(instructor.googleId, courseList);
                }
                
                courseList.add(instructor);
            }
        }
            
        AdminAccountManagementPageData data = new AdminAccountManagementPageData(account, instructorAccountsTable,
                                                                                 instructorCoursesTable, isToShowAll);
        
        statusToAdmin = "Admin Account Management Page Load<br>" 
                        + "<span class=\"bold\">Total Instructors:</span> " + instructorAccountsTable.size();
        
        return createShowPageResult(Const.ViewURIs.ADMIN_ACCOUNT_MANAGEMENT, data);
    }
    
}
