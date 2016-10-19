<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        {label: "${ui.message("patientlist.title")}"}
    ];

    jQuery('#breadcrumbs').html(emr.generateBreadcrumbHtml(breadcrumbs));
</script>
<span class="h1-substitue-left">
    ${ui.message('patientlist.page')}
</span>

<div>
    <div style="font-size:inherit">
        <br />
        <div class="row">
            <div class="col-xs-9">
                <div class="col-xs-4">
                    <strong>
                        ${ui.message('Select Patient List')}:
                    </strong>
                </div>

                <div class="col-xs-6">
                    <select class="form-control" ng-model="entity.patientList"
                            ng-options='patList.name for patList in patientList track by patList.uuid'
                            ng-change="getPatientListData(entity.patientList.uuid, currentPage, limit)">
                    </select>
                </div>

                <div class="col-xs-2">
                    <input type="button" value="Search" class="confirm" ng-click="getPatientListData(entity.patientList.uuid, currentPage, limit)">
                </div>
            </div>
        </div>
        <br/>
    </div>

    <div id="entities" class="detail-section-border-top">
        <br/>
        <table class="manage-entities-table">
            <tr class="clickable-tr" pagination-id="__patientListData"
                dir-paginate="entity in patientListData | itemsPerPage: limit"
                total-items="totalNumOfResults" current-page="currentPage">
                <td>
                    <div class="row" >
                        <div class="col-sm-10" style="width:99%;font-weight:bold; padding: 8px;margin-left:5px; background-color:#dddddd;">
                            {{entity.patient.display}} (<span ng-show="entity.patient.person.gender === 'M'">Male</span>
                            <span ng-show="entity.patient.person.gender === 'F'">Female</span>
                            - Age {{entity.patient.person.age}})
                        </div>
                    </div>

                    <div class="row" >
                        <div ng-show="entity.activeVisit !== null" class="col-sm-10" style="padding: 10px;">
                            Visit: {{entity.activeVisit.display}}
                        </div>
                    </div>

                    <div class="row" ng-show="entity.activeVisit !== null && entity.activeVisit.attributes !== []">
                        <div class="col-sm-12">
                            <span ng-repeat="visitAttribute in entity.activeVisit.attributes">
                                <span>
                                    {{visitAttribute.display}}<span ng-if="\$index < entity.activeVisit.attributes.length - 1">, </span>
                                </span>
                            </span>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
        ${ui.includeFragment("openhmis.commons", "paginationFragment", [
                paginationId      : "__patientListData",
                onPageChange      : "getPatientListData(entity.patientList.uuid, currentPage, limit)",
                onChange          : "getPatientListData(entity.patientList.uuid, currentPage, limit)",
                model             : "limit",
                pagingFrom        : "pagingFrom(currentPage, limit)",
                pagingTo          : "pagingTo(currentPage, limit, totalNumOfResults)",
                showRetiredSection: "false"
        ])}
    </div>
</div>



