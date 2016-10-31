<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        {label: "${ui.message("patientlist.title")}"}
    ];

    jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));
</script>
<span class="h1-substitue-left">
    ${ui.message('patientlist.page')}
    <br /><br />
</span>
<div>
    <ul id="patient-list" class="left-menu">
        <li class="menu-item viewPatientListDetails"
            ng-class="{'selected': patientList.selected}"
            ng-repeat="patientList in patientLists track by patientList.uuid"
            ng-click="getPatientListData(patientList, patientList.currentPage, limit, true)">
            <i ng-show="patientList.showSpinner === true" class="icon-spinner icon-spin icon-2x pull-left"></i>
            <span class="menu-title capitalize">{{patientList.name}}</span>
            <span class="arrow-border"></span>
            <span class="arrow"></span>
        </li>
    </ul>

    <div class="main-content">
        <table class="manage-entities-table">
            <tr class="clickable-tr" pagination-id="__patientListData"
                dir-paginate="entity in patientListData | itemsPerPage: limit"
                total-items="totalNumOfResults" current-page="patientList.currentPage">
                <td>
                    <div class="row" >
                        <div class="col-sm-10" style="width:99%;font-weight:bold; padding: 8px;margin-left:5px; background-color:#dddddd;">
                            {{entity.patient.display}} (<span ng-show="entity.patient.person.gender === 'M'">Male</span>
                            <span ng-show="entity.patient.person.gender === 'F'">Female</span>
                            - Age {{entity.patient.person.age}})
                        </div>
                    </div>

                    <div class="row" >
                        <div ng-show="entity.visit !== null" class="col-sm-10" style="padding: 10px;">
                            Visit: {{entity.visit.display}}
                        </div>
                    </div>

                    <div class="row" ng-show="entity.visit !== null && entity.visit.attributes !== []">
                        <div class="col-sm-12">
                            <span ng-repeat="visitAttribute in entity.visit.attributes">
                                <span>
                                    {{visitAttribute.display}}<span ng-if="\$index < entity.visit.attributes.length - 1">, </span>
                                </span>
                            </span>
                        </div>
                    </div>

                    <div class="row">
                        <div class="status-container">
                            <span ng-show="entity.visit.stopDatetime === null">
                                <span class="status active"></span>
                                ${ ui.message("coreapps.activeVisit") }
                            </span>

                            <a class="right"
                               href="/${ ui.contextPath() }/coreapps/clinicianfacing/patient.page?patientId={{entity.patient.uuid}}">Patient Details</a>
                            <span class="right"> | </span>

                            <a class="right" ng-show="entity.visit.stopDatetime === null"
                               href="/${ ui.contextPath() }/coreapps/patientdashboard/patientDashboard.page?patientId={{entity.patient.uuid}}">
                                Visit Details</a>
                            <span ng-show="entity.visit.stopDatetime === null" class="right"> | </span>

                            <a class="right" ng-show="entity.visit.stopDatetime !== null">Start Visit</a>
                            <a class="right" ng-show="entity.visit.stopDatetime === null">End Visit</a>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
        ${ui.includeFragment("openhmis.commons", "paginationFragment", [
                hide              : "totalNumOfResults === 0",
                paginationId      : "__patientListData",
                onPageChange      : "getPatientListData(patientList, patientList.currentPage, limit)",
                onChange          : "getPatientListData(patientList, patientList.currentPage, limit)",
                model             : "limit",
                pagingFrom        : "pagingFrom(patientList.currentPage || 1, limit)",
                pagingTo          : "pagingTo(patientList.currentPage || 1, limit, totalNumOfResults)",
                showRetiredSection: "false"
        ])}

    </div>

    <div id="entities" class="detail-section-border-top">
        <br/>
    </div>
</div>



