package org.baps.api.vtms.services;

import static org.baps.api.vtms.enumerations.RoleEnum.GUEST_VISIT_COORDINATOR;
import static org.baps.api.vtms.enumerations.RoleEnum.RELATIONSHIP_MANAGER;

import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.constants.NotificationConstant;
import org.baps.api.vtms.enumerations.NotificationTemplateEnum;
import org.baps.api.vtms.enumerations.RoleEnum;
import org.baps.api.vtms.enumerations.VisitStageEnum;
import org.baps.api.vtms.enumerations.VisitorContactTypeEnum;
import org.baps.api.vtms.mappers.PersonnelMapper;
import org.baps.api.vtms.models.ChildLookupModel;
import org.baps.api.vtms.models.StageModel;
import org.baps.api.vtms.models.entities.Lookup;
import org.baps.api.vtms.models.entities.NotificationTemplate;
import org.baps.api.vtms.models.entities.Personnel;
import org.baps.api.vtms.models.entities.ServiceTemplate;
import org.baps.api.vtms.models.entities.Visit;
import org.baps.api.vtms.models.entities.VisitPersonnel;
import org.baps.api.vtms.models.entities.VisitService;
import org.baps.api.vtms.models.entities.VisitVisitor;
import org.baps.api.vtms.models.notification.NotificationCampaignModel;
import org.baps.api.vtms.models.notification.NotificationUser;
import org.baps.api.vtms.repositories.PersonnelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationComposeService {

    private static final String VAR_PRIMARY_VISITOR = "${PRIMARY_VISITOR}";

    private static final String VAR_ORGANIZATION_NAME = "${ORGANIZATION_NAME}";

    private static final String VAR_TYPE_OF_VISIT = "${TYPE_OF_VISIT}";

    private static final String VAR_TOUR_TYPE = "${TOUR_TYPE}";

    private static final String VAR_SERVICES_NAME = "${SERVICES_NAME}";

    private static final String VAR_RM = "${RM}";

    private static final String VAR_VC = "${VC}";

    private static final String VAR_MULAKAT = "${MULAKAT}";

    private static final String VAR_NO_OF_GUESTS = "${NO_OF_GUESTS}";

    private static final String VAR_VISIT_TIME = "${VISIT_TIME}";

    private static final String NA = "N/A";

    private static final String DYNAMIC_VISIT_BODY =
        """
        <div style="border-radius: 7px; margin-bottom: 16px; background-color: #ffffff; padding: 20px; max-width: 600px; 
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
            <p style="margin: 12px 0; margin-top: 0; font-size: 18px; font-weight: 600; line-height: 1.5; color: #333;">
        """
            +
            VAR_PRIMARY_VISITOR
            +
            """
            </p>
            <p style="margin: 4px 0; font-size: 16px; line-height: 1.5; color: #333;">
            """
            + VAR_ORGANIZATION_NAME + " | " + VAR_TYPE_OF_VISIT
            + """
            </p>
            <p style="margin: 4px 0; font-size: 16px; line-height: 1.5; color: #333;">
            """
            + VAR_TOUR_TYPE + " | " + VAR_SERVICES_NAME
            +
            """
                </p>
                <p style="margin: 4px 0; font-size: 16px; line-height: 1.5; color: #333;">
                    RM:""" + " " + VAR_RM
            +
            """
            </p>
            <p style="margin: 4px 0; font-size: 16px; line-height: 1.5; color: #333;">
                VC:""" + " "  + VAR_VC
            +
            """
            </p>
            <p style="margin: 4px 0; font-size: 16px; line-height: 1.5; color: #333;">
                Mulakat:""" + " "  + VAR_MULAKAT
            +
            """
            </p>
            <p style="margin: 4px 0; font-size: 16px; line-height: 1.5; color: #333;">
                No. of guests:""" + " "  + VAR_NO_OF_GUESTS
            +
            """
            </p>
            <p style="margin: 4px 0; font-size: 16px; line-height: 1.5; color: #333;">
                Visit time:""" + " "  + VAR_VISIT_TIME
            +
            """
            </p>
        </div>
        """;

    private final PersonnelRepository personnelRepository;

    private final NotificationService notificationService;

    private final ServiceTemplateService serviceTemplateService;

    private final NotificationTemplateService notificationTemplateService;

    private final LookupService lookupService;

    @Value("${front.end.baseurl}")
    private String frontEndUrl;

    @Value("${front.end.public-baseurl}")
    private String frontEndPublicUrl;

    private final PersonnelMapper personnelMapper;

    /**
     * Sends a notification campaign based on a notification template and a list of users.
     *
     * @param notificationTemplateEnum The enumeration representing the notification template to use.
     * @param users                    A list of NotificationUser objects to receive the notification.
     */
    public void sendCampaign(final NotificationTemplateEnum notificationTemplateEnum, final List<NotificationUser> users) {

        final Optional<NotificationTemplate> optionalNotificationTemplate = notificationTemplateService
            .findByNotificationTemplate(notificationTemplateEnum);

        if (CollectionUtils.isNotEmpty(users) && optionalNotificationTemplate.isPresent()) {

            final NotificationCampaignModel emailNotificationCampaignModel = NotificationCampaignModel.builder()
                .templateId(optionalNotificationTemplate.get().getTemplateId())
                .templateVersion(optionalNotificationTemplate.get().getVersion())
                .channel(optionalNotificationTemplate.get().getNotificationChannelEnum())
                .users(users)
                .build();

            notificationService.sendCampaign(emailNotificationCampaignModel);
        }
    }

    /**
     * Retrieves the full name of a personnel if available, or an empty string otherwise.
     *
     * @param personnelOptional An optional {@link Personnel} object.
     * @return The full name of the personnel if available, otherwise an empty string.
     */
    private String getPersonnelOptionalName(final Optional<Personnel> personnelOptional) {
        return personnelOptional.map(personnel -> personnel.getFirstName() + " " + personnel.getLastName()).orElse("");
    }

    /**
     * Retrieves an Optional VisitPersonnel from a list based on the specified RoleEnum.
     *
     * @param visitPersonnelList List of VisitPersonnel objects to search.
     * @param roleEnum           RoleEnum used for filtering.
     * @return Optional containing the first matching VisitPersonnel, or empty if not found.
     */
    private Optional<VisitPersonnel> getVisitPersonnelOptionalByRoleEnum(final List<VisitPersonnel> visitPersonnelList,
                                                                         final RoleEnum roleEnum) {

        if (CollectionUtils.isNotEmpty(visitPersonnelList) && ObjectUtils.isNotEmpty(roleEnum)) {
            return visitPersonnelList.stream()
                .filter(visitPersonnel -> visitPersonnel.getRole().getUucode().equals(roleEnum.name()))
                .findFirst();
        }
        return Optional.empty();
    }

    /**
     * Retrieves an optional {@link VisitVisitor} based on the specified {@link VisitorContactTypeEnum}
     * from the list of visit visitors associated with a {@link Visit} object.
     *
     * @param visitorContactTypeEnum The {@link VisitorContactTypeEnum} to match against.
     * @param visit                  The {@link Visit} object containing the list of visit visitors.
     * @return An optional {@link VisitVisitor} object representing the first visit visitor with, or an empty optional if not found.
     */
    private Optional<VisitVisitor> getVisitVisitorOptionalByVisitorContactType(final Visit visit,
                                                                               final VisitorContactTypeEnum visitorContactTypeEnum) {

        return visit.getVisitVisitorList().stream()
            .filter(visitVisitor -> visitVisitor.getVisitorContactTypeEnum().equals(visitorContactTypeEnum))
            .findFirst();
    }

    /**
     * Retrieves the full name of the primary visitor associated with a specific {@link VisitorContactTypeEnum} in a given {@link Visit}.
     *
     * @param visitorContactTypeEnum The {@link VisitorContactTypeEnum} specifying the type of visitor for which the name is needed.
     * @param visit                  The {@link Visit} object containing information about the visit and its visitors.
     * @return The full name of the visitor, or an empty string if the visitor is not found.
     */
    private String getVisitorNameByVisitorContactType(final Visit visit, final VisitorContactTypeEnum visitorContactTypeEnum) {

        return getVisitVisitorOptionalByVisitorContactType(visit, visitorContactTypeEnum)
            .map(visitVisitor -> visitVisitor.getVisitor().getFirstName() + " " + visitVisitor.getVisitor().getLastName())
            .orElse("");
    }

    /**
     * Retrieves the personnel who created the visit, if available.
     *
     * @param visit The {@link Visit} object for which the creator's personnel information is needed.
     * @return An optional {@link Personnel} object representing the creator of the visit.
     */
    private Optional<Personnel> getCreatedByPersonnelOptional(final Visit visit) {
        return StringUtils.isNotBlank(visit.getCreatedBy())
            ? personnelRepository.findById(visit.getCreatedBy())
            : Optional.empty();
    }

    /**
     * Retrieves the requester's name for a visit, either from the creator's personnel information
     * or the primary visitor's information.
     *
     * @param visit The {@link Visit} object for which the requester's name is needed.
     * @return The requester's name if available, otherwise an empty string.
     */
    private String getRequesterName(final Visit visit) {
        return getCreatedByPersonnelOptional(visit).map(personnel -> personnel.getFirstName() + " " + personnel.getLastName())
            .orElse(getVisitorNameByVisitorContactType(visit, VisitorContactTypeEnum.PRIMARY));
    }

    /**
     * Retrieves the organization name of the primary visitor for a visit.
     *
     * @param visit The {@link Visit} object for which the organization name is needed.
     * @return The organization name of the primary visitor if available, otherwise an empty string.
     */
    private String getOrganizationName(final Visit visit) {

        return getVisitVisitorOptionalByVisitorContactType(visit, VisitorContactTypeEnum.PRIMARY)
            .map(visitVisitor -> visitVisitor.getVisitor().getOrganizationName())
            .orElse("");
    }

    /**
     * Retrieves a formatted string of requested services from a {@link Visit}.
     *
     * <p>
     * Returns a comma-separated string of requested services if available; otherwise, an empty string.
     * In case of errors, logs the error and returns "ERROR".
     * </p>
     *
     * @param visit The {@link Visit} object containing requested services information.
     * @return A formatted string of requested services or an empty string in case of errors.
     */
    private String getRequestedServices(final Visit visit) {
        try {
            if (CollectionUtils.isNotEmpty(visit.getRequestedServiceIds())) {

                final List<ServiceTemplate> existingServiceTemplates = serviceTemplateService
                        .findByServiceTemplateIdsAndSiteUUCode(visit.getRequestedServiceIds(), visit.getSite().getUuCode());
                
                final Set<String> requestedServices = existingServiceTemplates.stream()
                        .map(ServiceTemplate::getName).collect(Collectors.toSet());
                
                return String.join(", ", requestedServices);
            } else {
                return "";
            }
        } catch (final Exception e) {
            log.error("Error while getting visit requested services for email. Error message : {}", e.getMessage());
            return "ERROR";
        }
    }

    /**
     * Gets the reason for a visit stage from the provided Visit.
     *
     * @param visit          The Visit object.
     * @param visitStageEnum The target VisitStageEnum.
     * @return The reason for the specified visit stage, or an empty string if not found.
     */
    public String getStageReason(final Visit visit, final VisitStageEnum visitStageEnum) {

        String visitStageReason = "";

        if (CollectionUtils.isNotEmpty(visit.getStageModelList())) {
            visitStageReason = visit.getStageModelList().stream()
                .filter(stageModel -> stageModel.getStage().equals(visitStageEnum.name())
                    && StringUtils.isNotBlank(stageModel.getReason()))
                .findFirst()
                .map(StageModel::getReason).orElse("");
        }
        return visitStageReason;
    }

    /**
     * Maps specific details from a {@link Visit} object to a {@link Map} based on the provided body variables.
     *
     * @param visit    The {@link Visit} object containing information to be mapped.
     * @param bodyVars The set of body variables specifying the details to be included in the result map.
     * @return A {@link Map} containing the mapped details based on the provided body variables.
     */
    private Map<String, String> mapVisitDetails(final Visit visit, final Set<String> bodyVars) {

        final Map<String, String> resultMap = new HashMap<>();

        bodyVars.forEach(bodyVar -> {

            switch (bodyVar) {
                case NotificationConstant.VISIT_REQUEST_NUMBER -> resultMap.put(bodyVar, visit.getRequestNumber());

                case NotificationConstant.TYPE_OF_VISIT -> resultMap.put(bodyVar, visit.getTypeOfVisit());

                case NotificationConstant.VISIT_PAGE_LINK, NotificationConstant.VISIT_DETAIL_PAGE_LINK ->
                    resultMap.put(bodyVar, frontEndUrl + NotificationConstant.VISIT_DETAIL_PATH + visit.getVisitId());
                    
                case NotificationConstant.TOTAL_VISITOR -> resultMap.put(bodyVar, visit.getTotalVisitors().toString());

                case NotificationConstant.VISIT_START_DATE ->
                    resultMap.put(bodyVar, visit.getStartDateTime().format(GeneralConstant.DATE_FORMATTER_MM_DD_YYYY));

                case NotificationConstant.VISIT_START_TIME ->
                    resultMap.put(bodyVar, visit.getStartDateTime().format(GeneralConstant.TIME_FORMAT_12_FORMATTER));

                case NotificationConstant.VISIT_END_TIME ->
                    resultMap.put(bodyVar, visit.getEndDateTime().format(GeneralConstant.TIME_FORMAT_12_FORMATTER));

                case NotificationConstant.REQUESTER_NAME -> resultMap.put(bodyVar, getRequesterName(visit));

                case NotificationConstant.PRIMARY_VISITOR_NAME -> resultMap.put(bodyVar,
                    getVisitorNameByVisitorContactType(visit, VisitorContactTypeEnum.PRIMARY));

                case NotificationConstant.ORGANIZATION_NAME -> resultMap.put(bodyVar, getOrganizationName(visit));

                case NotificationConstant.REQUESTED_SERVICES -> resultMap.put(bodyVar, getRequestedServices(visit));

                case NotificationConstant.VISIT_DECLINED_STAGE_REASON ->
                    resultMap.put(bodyVar, getStageReason(visit, VisitStageEnum.DECLINED));

                case NotificationConstant.VISIT_CANCELLED_STAGE_REASON ->
                    resultMap.put(bodyVar, getStageReason(visit, VisitStageEnum.CANCELLED));

                case NotificationConstant.VISIT_ADMIN_NAME -> resultMap.put(bodyVar, getPersonnelOptionalName(
                    getVisitPersonnelOptionalByRoleEnum(visit.getVisitPersonnelList(), RoleEnum.VISIT_ADMIN)
                        .map(VisitPersonnel::getPersonnel)));

                case NotificationConstant.VISIT_ADMIN_PHONE_NUMBER -> resultMap.put(bodyVar,
                    getVisitPersonnelOptionalByRoleEnum(visit.getVisitPersonnelList(), RoleEnum.VISIT_ADMIN)
                        .map(VisitPersonnel::getPersonnel).map(Personnel::getPhoneNumber).orElse(""));

                case NotificationConstant.GUEST_VISIT_COORDINATOR_NAME -> resultMap.put(bodyVar, getPersonnelOptionalName(
                    getVisitPersonnelOptionalByRoleEnum(visit.getVisitPersonnelList(), RoleEnum.GUEST_VISIT_COORDINATOR)
                        .map(VisitPersonnel::getPersonnel)));

                case NotificationConstant.GUEST_VISIT_COORDINATOR_PHONE_NUMBER -> resultMap.put(bodyVar,
                    getVisitPersonnelOptionalByRoleEnum(visit.getVisitPersonnelList(), RoleEnum.GUEST_VISIT_COORDINATOR)
                        .map(VisitPersonnel::getPersonnel).map(Personnel::getPhoneNumber).orElse(""));
                
                case NotificationConstant.PUBLIC_VISIT_PAGE_LINK -> {
                    if (!visit.isPrivate()) {
                        if (ObjectUtils.isNotEmpty(visit.getTourSlot()) && StringUtils.isNotBlank(visit.getTourSlot().getTourSlotId())) {
                            resultMap.put(bodyVar, frontEndPublicUrl + NotificationConstant.HOURLY_VISIT_PATH + visit.getVisitId());
                        } else {
                            resultMap.put(bodyVar, frontEndPublicUrl + NotificationConstant.TOUR_VISIT_PATH + visit.getVisitId());
                        }
                    }
                }

                default -> {
                }
            }
        });

        return resultMap;
    }

    /**
     * Sends notification for visit approval pending to super admin personnel.
     *
     * @param visit The {@link Visit} object for which the approval is pending.
     * @param siteUUCode The unique code associated with the site.
     */
    public void sendVisitApprovalPending(final Visit visit, final String siteUUCode) {

        final NotificationTemplateEnum notificationTemplateEnum = NotificationTemplateEnum.VISIT_APPROVAL_PENDING_EMAIL;

        final List<Personnel> visitAdminPersonnelList =
            personnelRepository.findAllByPersonnelRoleListRoleUucodeInAndPersonnelRoleListSiteUuCode(
                Set.of(RoleEnum.SUPER_ADMIN.name()), siteUUCode);

        if (CollectionUtils.isNotEmpty(visitAdminPersonnelList)) {

            final List<NotificationUser> notificationUserList = visitAdminPersonnelList.stream()
                .map(personnel -> NotificationUser.builder()
                    .email(personnelMapper.mapEmailByPersonnel(personnel))
                    .emailType(NotificationConstant.EMAIL_TYPE_PRIMARY)
                    .bodyVars(mapVisitDetails(visit, notificationTemplateEnum.getBodyVars()))
                    .build()).toList();

            sendCampaign(notificationTemplateEnum, notificationUserList);
        }
    }

    /**
     * Sends notification for visit request received to the primary visitor.
     *
     * @param visit The {@link Visit} object for which the request is received.
     */
    public void sendVisitRequestReceived(final Visit visit) {

        final NotificationTemplateEnum notificationTemplateEnum = NotificationTemplateEnum.VISIT_REQUEST_RECEIVED_EMAIL;

        final List<NotificationUser> notificationUserList = new ArrayList<>();

        final Map<String, String> bodyVars = mapVisitDetails(visit, notificationTemplateEnum.getBodyVars());

        // Primary Visitor
        getNotificationUserByVisitorContactType(visit, VisitorContactTypeEnum.PRIMARY, notificationTemplateEnum, bodyVars)
            .ifPresent(notificationUserList::add);

        // Secondary Visitor
        getNotificationUserByVisitorContactType(visit, VisitorContactTypeEnum.SECONDARY, notificationTemplateEnum, bodyVars)
            .ifPresent(notificationUserList::add);

        sendCampaign(notificationTemplateEnum, notificationUserList);
    }


    /**
     * Sends notification for successful creation of a visit to relevant personnel.
     *
     * @param visit The {@link Visit} object that has been created successfully.
     */
    public void sendVisitCreatedSuccessfully(final Visit visit) {

        final NotificationTemplateEnum notificationTemplateEnum = NotificationTemplateEnum.VISIT_CREATED_SUCCESSFULLY_EMAIL;

        final List<NotificationUser> notificationUserList = new ArrayList<>();

        final Map<String, String> bodyVarsMap = mapVisitDetails(visit, notificationTemplateEnum.getBodyVars());

        // Created By
        getCreatedByPersonnelOptional(visit).ifPresent(personnel -> {

            if (notificationTemplateEnum.getBodyVars().contains(NotificationConstant.RECIPIENT_NAME)) {
                bodyVarsMap.put(NotificationConstant.RECIPIENT_NAME, getPersonnelOptionalName(Optional.of(personnel)));
            }

            notificationUserList.add(NotificationUser.builder()
                .email(personnelMapper.mapEmailByPersonnel(personnel))
                .emailType(NotificationConstant.EMAIL_TYPE_PRIMARY)
                .bodyVars(new HashMap<>(bodyVarsMap))
                .build());
        });

        // Relationship Manager
        getVisitPersonnelOptionalByRoleEnum(visit.getVisitPersonnelList(), RELATIONSHIP_MANAGER)
            .ifPresent(visitPersonnel -> {

                if (notificationTemplateEnum.getBodyVars().contains(NotificationConstant.RECIPIENT_NAME)) {
                    bodyVarsMap.put(NotificationConstant.RECIPIENT_NAME,
                        getPersonnelOptionalName(Optional.of(visitPersonnel.getPersonnel())));
                }

                notificationUserList.add(NotificationUser.builder()
                    .email(personnelMapper.mapEmailByPersonnel(visitPersonnel.getPersonnel()))
                    .emailType(NotificationConstant.EMAIL_TYPE_PRIMARY)
                    .bodyVars(new HashMap<>(bodyVarsMap))
                    .build());
            });

        sendCampaign(notificationTemplateEnum, notificationUserList);
    }

    /**
     * Sends a confirmation notification for a scheduled visit.
     *
     * @param visit The visit for which the confirmation notification is being sent.
     */
    public void sendVisitConfirmationNotification(final Visit visit) {

        final NotificationTemplateEnum notificationTemplateEnum = NotificationTemplateEnum.VISIT_CONFIRMATION_EMAIL;

        final List<NotificationUser> notificationUserList = new ArrayList<>();

        final Map<String, String> bodyVarsMap = mapVisitDetails(visit, notificationTemplateEnum.getBodyVars());

        if (notificationTemplateEnum.getBodyVars().contains(NotificationConstant.HELP_DESK_NUMBER)) {
            bodyVarsMap.put(NotificationConstant.HELP_DESK_NUMBER, NotificationConstant.BAPS_HELP_DESK_NUMBER);
        }

        // Primary Visitor
        getNotificationUserByVisitorContactType(visit, VisitorContactTypeEnum.PRIMARY, notificationTemplateEnum, bodyVarsMap)
            .ifPresent(notificationUserList::add);

        // Secondary Visitor
        getNotificationUserByVisitorContactType(visit, VisitorContactTypeEnum.SECONDARY, notificationTemplateEnum, bodyVarsMap)
            .ifPresent(notificationUserList::add);

        sendCampaign(notificationTemplateEnum, notificationUserList);
    }

    /**
     * Retrieves a notification user for visit based on the specified details and template.
     *
     * @param visit                    The Visit for notification.
     * @param visitorContactTypeEnum   The contact type for the visitor.
     * @param notificationTemplateEnum The template for the notification.
     * @param bodyVarsMap              A Map containing key-value pairs representing variables for the notification body.
     * @return Optional notification user, or empty if not applicable.
     */
    private Optional<NotificationUser> getNotificationUserByVisitorContactType(final Visit visit,
                                                                               final VisitorContactTypeEnum visitorContactTypeEnum,
                                                                               final NotificationTemplateEnum notificationTemplateEnum,
                                                                               final Map<String, String> bodyVarsMap) {

        return getVisitVisitorOptionalByVisitorContactType(visit, visitorContactTypeEnum)
            .map(visitVisitor -> {

                if (notificationTemplateEnum.getBodyVars().contains(NotificationConstant.RECIPIENT_NAME)) {
                    bodyVarsMap.put(NotificationConstant.RECIPIENT_NAME,
                        getVisitorNameByVisitorContactType(visit, visitorContactTypeEnum));
                }

                return Optional.of(NotificationUser.builder()
                    .email(visitVisitor.getVisitor().getEmail())
                    .emailType(NotificationConstant.EMAIL_TYPE_PRIMARY)
                    .bodyVars(new HashMap<>(bodyVarsMap))
                    .build());
            }).orElse(Optional.empty());
    }

    /**
     * Sends new visit assigned notifications to personnel based on the specified template.
     *
     * @param visitPersonnelList List of VisitPersonnel to notify.
     */
    public void sendNewVisitAssigned(final List<VisitPersonnel> visitPersonnelList) {

        final NotificationTemplateEnum notificationTemplateEnum = NotificationTemplateEnum.NEW_VISIT_ASSIGNED_EMAIL;

        final List<NotificationUser> notificationUserList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(visitPersonnelList)) {

            final Map<String, String> bodyVarsMap =
                new HashMap<>(mapVisitDetails(visitPersonnelList.get(0).getVisit(), notificationTemplateEnum.getBodyVars()));

            visitPersonnelList.forEach(visitPersonnel ->
                getNotificationUserOptionalByVisitPersonnel(visitPersonnel, notificationTemplateEnum, bodyVarsMap)
                    .ifPresent(notificationUserList::add));
        }

        sendCampaign(notificationTemplateEnum, notificationUserList);
    }

    /**
     * Retrieves a notification user based on the specified details and template.
     *
     * @param visitPersonnel           The VisitPersonnel to notify.
     * @param notificationTemplateEnum The template for the notification.
     * @param bodyVarsMap              A Map containing key-value pairs representing variables for the notification body.
     * @return Optional notification user, or empty if not applicable.
     */
    private Optional<NotificationUser> getNotificationUserOptionalByVisitPersonnel(
        final VisitPersonnel visitPersonnel, final NotificationTemplateEnum notificationTemplateEnum,
        final Map<String, String> bodyVarsMap) {

        if (ObjectUtils.isNotEmpty(visitPersonnel)) {

            if (notificationTemplateEnum.getBodyVars().contains(NotificationConstant.RECIPIENT_NAME)) {
                bodyVarsMap.put(NotificationConstant.RECIPIENT_NAME,
                    getPersonnelOptionalName(Optional.of(visitPersonnel.getPersonnel())));
            }

            if (notificationTemplateEnum.getBodyVars().contains(NotificationConstant.ROLE_NAME)) {
                bodyVarsMap.put(NotificationConstant.ROLE_NAME, visitPersonnel.getRole().getName());
            }

            return Optional.of(NotificationUser.builder()
                .email(personnelMapper.mapEmailByPersonnel(visitPersonnel.getPersonnel()))
                .emailType(NotificationConstant.EMAIL_TYPE_PRIMARY)
                .bodyVars(new HashMap<>(bodyVarsMap))
                .build());
        }
        return Optional.empty();
    }

    /**
     * Sends visit accepted successfully notifications to relevant personnel based on the specified template.
     *
     * @param visit The Visit for notification.
     */
    public void sendVisitAcceptedSuccessfully(final Visit visit) {
        final NotificationTemplateEnum notificationTemplateEnum = NotificationTemplateEnum.VISIT_ACCEPTED_SUCCESSFULLY_EMAIL;

        final List<NotificationUser> notificationUserList = new ArrayList<>();

        final Map<String, String> bodyVarsMap = mapVisitDetails(visit, notificationTemplateEnum.getBodyVars());

        // CreateBy
        getCreatedByPersonnelOptional(visit)
            .flatMap(personnel -> getNotificationUserByPersonnel(personnel, visit, notificationTemplateEnum, bodyVarsMap))
            .ifPresent(notificationUserList::add);

        // Visit Admin
        getVisitPersonnelOptionalByRoleEnum(visit.getVisitPersonnelList(), RoleEnum.VISIT_ADMIN)
            .flatMap(visitPersonnel ->
                getNotificationUserByPersonnel(visitPersonnel.getPersonnel(), visit, notificationTemplateEnum, bodyVarsMap))
            .ifPresent(notificationUserList::add);

        sendCampaign(notificationTemplateEnum, notificationUserList);
    }

    /**
     * Retrieves a notification user based on the specified details and template.
     *
     * @param personnel                The Personnel to notify.
     * @param visit                    The Visit for notification.
     * @param notificationTemplateEnum The template for the notification.
     * @param bodyVarsMap              A Map containing key-value pairs representing variables for the notification body.
     * @return Optional notification user, or empty if not applicable.
     */
    private Optional<NotificationUser> getNotificationUserByPersonnel(
        final Personnel personnel, final Visit visit, final NotificationTemplateEnum notificationTemplateEnum,
        final Map<String, String> bodyVarsMap) {

        if (ObjectUtils.isNotEmpty(personnel) && ObjectUtils.isNotEmpty(visit)) {

            if (notificationTemplateEnum.getBodyVars().contains(NotificationConstant.RECIPIENT_NAME)) {
                bodyVarsMap.put(NotificationConstant.RECIPIENT_NAME, getPersonnelOptionalName(Optional.of(personnel)));
            }

            return Optional.of(NotificationUser.builder()
                .email(personnelMapper.mapEmailByPersonnel(personnel))
                .emailType(NotificationConstant.EMAIL_TYPE_PRIMARY)
                .bodyVars(new HashMap<>(bodyVarsMap))
                .build());
        }
        return Optional.empty();
    }

    /**
     * Sends a notification to relevant parties when a visit is DECLINED.
     *
     * @param visit The visit that has been canceled.
     */
    public void sendVisitDeclinedNotification(final Visit visit) {

        final NotificationTemplateEnum notificationTemplateEnum = NotificationTemplateEnum.VISIT_DECLINED_EMAIL;

        final List<NotificationUser> notificationUserList = new ArrayList<>();

        final Map<String, String> bodyVarsMap = mapVisitDetails(visit, notificationTemplateEnum.getBodyVars());

        // Primary Visitor
        getNotificationUserByVisitorContactType(visit, VisitorContactTypeEnum.PRIMARY, notificationTemplateEnum, bodyVarsMap)
            .ifPresent(notificationUserList::add);

        // Secondary Visitor
        getNotificationUserByVisitorContactType(visit, VisitorContactTypeEnum.SECONDARY, notificationTemplateEnum, bodyVarsMap)
            .ifPresent(notificationUserList::add);

        sendCampaign(notificationTemplateEnum, notificationUserList);
    }
    
    public void sendVisitCancelledNotification(final Visit visit) {
        sendVisitCancelledByAdminNotification(visit);
        sendVisitCancelledSendToVisitorNotification(visit);
    }

    /**
     * Sends a notification to relevant parties when a visit is canceled.
     *
     * @param visit The visit that has been canceled.
     */
    public void sendVisitCancelledByAdminNotification(final Visit visit) {

        final NotificationTemplateEnum notificationTemplateEnum;

        final List<NotificationUser> notificationUserList = new ArrayList<>();

        notificationTemplateEnum = NotificationTemplateEnum.VISIT_CANCELLED_BY_ADMIN_EMAIL;

        final Map<String, String> bodyVarsMap = new HashMap<>(mapVisitDetails(visit, notificationTemplateEnum.getBodyVars()));

        // CreateBy
        getCreatedByPersonnelOptional(visit)
            .flatMap(personnel -> getNotificationUserByPersonnel(personnel, visit, notificationTemplateEnum, bodyVarsMap))
            .ifPresent(notificationUserList::add);

        // all visit personnel
        if (CollectionUtils.isNotEmpty(visit.getVisitPersonnelList())) {

            visit.getVisitPersonnelList()
                .forEach(visitPersonnel ->
                getNotificationUserByPersonnel(visitPersonnel.getPersonnel(), visit, notificationTemplateEnum, bodyVarsMap)
                .ifPresent(notificationUserList::add));
        }

        sendCampaign(notificationTemplateEnum, notificationUserList);
    }
    
    /**
     * Sends a notification to relevant parties when a visit is canceled.
     *
     * @param visit The visit that has been canceled.
     */
    public void sendVisitCancelledSendToVisitorNotification(final Visit visit) {

        final NotificationTemplateEnum notificationTemplateEnum;

        final List<NotificationUser> notificationUserList = new ArrayList<>();

        notificationTemplateEnum = NotificationTemplateEnum.VISIT_CANCELLED_SEND_EMAIL_TO_VISITOR;

        final Map<String, String> bodyVarsMap = new HashMap<>(mapVisitDetails(visit, notificationTemplateEnum.getBodyVars()));

        // Primary Visitor
        getNotificationUserByVisitorContactType(visit, VisitorContactTypeEnum.PRIMARY, notificationTemplateEnum, bodyVarsMap)
            .ifPresent(notificationUserList::add);

        // Secondary Visitor
        getNotificationUserByVisitorContactType(visit, VisitorContactTypeEnum.SECONDARY, notificationTemplateEnum, bodyVarsMap)
            .ifPresent(notificationUserList::add);

        sendCampaign(notificationTemplateEnum, notificationUserList);
    }


    /**
     * Sends a notification for a visit assignment to the specified personnel.
     *
     * @param visit              The visit for which the notification is being sent.
     * @param visitPersonnelList The list of personnel associated with the visit.
     * @param serviceName        The name of the service related to the visit.
     */
    public void sendVisitAssignedNotification(
        final Visit visit, final List<VisitPersonnel> visitPersonnelList, final String serviceName) {

        if (CollectionUtils.isNotEmpty(visitPersonnelList)) {

            final NotificationTemplateEnum notificationTemplateEnum = NotificationTemplateEnum.VISIT_ASSIGNED_EMAIL;

            final Map<String, String> bodyVars = new HashMap<>(mapVisitDetails(visit, notificationTemplateEnum.getBodyVars()));

            final List<NotificationUser> notificationUserList = new ArrayList<>();

            if (notificationTemplateEnum.getBodyVars().contains(NotificationConstant.SERVICE_NAME)) {

                bodyVars.put(NotificationConstant.SERVICE_NAME, serviceName);
            }

            visitPersonnelList.forEach(visitPersonnel ->
                getNotificationUserOptionalByVisitPersonnel(visitPersonnel, notificationTemplateEnum, bodyVars)
                    .ifPresent(notificationUserList::add));

            sendCampaign(notificationTemplateEnum, notificationUserList);
        }
    }

    /**
     * Sends a meeting notification to the specified personnel associated with the given {@link VisitService}.
     *
     * @param visitService     The {@link VisitService} for which the meeting notification is being sent.
     * @param meetingPersonnel The {@link Personnel} to receive the meeting notification.
     */
    public void sendMeetingWithGuestNotification(final VisitService visitService, final Personnel meetingPersonnel) {

        final NotificationTemplateEnum notificationTemplateEnum = NotificationTemplateEnum.MEETING_WITH_GUEST_EMAIL;

        if (ObjectUtils.isNotEmpty(meetingPersonnel)) {

            final Map<String, String> bodyVars =
                new HashMap<>(mapVisitDetails(visitService.getVisit(), notificationTemplateEnum.getBodyVars()));

            if (notificationTemplateEnum.getBodyVars().contains(NotificationConstant.RECIPIENT_NAME)) {
                bodyVars.put(NotificationConstant.RECIPIENT_NAME, getPersonnelOptionalName(Optional.of(meetingPersonnel)));
            }

            if (notificationTemplateEnum.getBodyVars().contains(NotificationConstant.MEETING_START_TIME)) {
                bodyVars.put(NotificationConstant.MEETING_START_TIME,
                    visitService.getStartDateTime().format(GeneralConstant.TIME_FORMAT_12_FORMATTER));
            }

            if (notificationTemplateEnum.getBodyVars().contains(NotificationConstant.MEETING_END_TIME)) {
                bodyVars.put(NotificationConstant.MEETING_END_TIME,
                    visitService.getEndDateTime().format(GeneralConstant.TIME_FORMAT_12_FORMATTER));
            }

            sendCampaign(notificationTemplateEnum, List.of(NotificationUser.builder()
                .email(personnelMapper.mapEmailByPersonnel(meetingPersonnel))
                .emailType(NotificationConstant.EMAIL_TYPE_PRIMARY)
                .bodyVars(new HashMap<>(bodyVars))
                .build()));
        }
    }

    /**
     * Sends visit feedback notifications based on the provided list of {@link Visit} instances.
     *
     * @param visitList A list of {@link Visit} instances for which visit feedback notifications are sent.
     */
    public void sendVisitFeedbackNotification(final List<Visit> visitList) {

        final NotificationTemplateEnum notificationTemplateEnum = NotificationTemplateEnum.VISIT_FEEDBACK_EMAIL;

        if (CollectionUtils.isNotEmpty(visitList)) {

            visitList.stream().filter(visit -> ObjectUtils.isNotEmpty(visit.getVisitFeedback()))
                .forEach(visit -> {

                    final List<NotificationUser> notificationUserList = new ArrayList<>();

                    final Map<String, String> bodyVarsMap = mapVisitDetails(visit, notificationTemplateEnum.getBodyVars());

                    if (notificationTemplateEnum.getBodyVars().contains(NotificationConstant.VISIT_FEEDBACK_LINK)) {

                        bodyVarsMap.put(NotificationConstant.VISIT_FEEDBACK_LINK,
                            frontEndUrl + NotificationConstant.VISIT_FEEDBACK_PATH
                                + "?id=" + visit.getVisitFeedback().getVisitFeedbackId());
                    }

                    // Primary Visitor
                    getNotificationUserByVisitorContactType(visit, VisitorContactTypeEnum.PRIMARY, notificationTemplateEnum, bodyVarsMap)
                        .ifPresent(notificationUserList::add);

                    // Secondary Visitor
                    getNotificationUserByVisitorContactType(visit, VisitorContactTypeEnum.SECONDARY, notificationTemplateEnum, bodyVarsMap)
                        .ifPresent(notificationUserList::add);

                    sendCampaign(notificationTemplateEnum, notificationUserList);
                });
        }
    }

    public void sendNextDayVisitListNotification(final List<Visit> visitList, final String visitDate) {

        final NotificationTemplateEnum notificationTemplateEnum = NotificationTemplateEnum.DAILY_VISIT_EMAIL;

        // fetch the lookup object of DAILY_VISIT_EMAIL
        final Lookup dailyVisitEmailLookup = lookupService.findLookupByKey(notificationTemplateEnum.name());

        // fetch the list of emails from dailyVisitEmailLookup to whom we want to send the visit list.
        final List<ChildLookupModel> childLookupModelList = dailyVisitEmailLookup.getChildLookupModelList();

        final List<String> emailList = childLookupModelList.stream().map(ChildLookupModel::getKey).toList();

        final List<NotificationUser> notificationUserList = new ArrayList<>();

        final String visitListData = prepareDynamicVisitBody(visitList);

        emailList.forEach(email -> {
            final NotificationUser user = NotificationUser.builder()
                .email(email)
                .emailType(NotificationConstant.EMAIL_TYPE_PRIMARY)
                .titleVars(Map.of(NotificationConstant.VISIT_DATE, visitDate))
                .bodyVars(Map.of(NotificationConstant.VISIT_DATE, visitDate, NotificationConstant.VISIT_LIST_DATA, visitListData))
                .build();
            notificationUserList.add(user);
        });

        sendCampaign(notificationTemplateEnum, notificationUserList);
    }

    private String prepareDynamicVisitBody(final List<Visit> visitList) {
        final StringBuilder visitListStringBuilder = new StringBuilder();
        for (final Visit visit : visitList) {
            final StringBuilder visitStringBuilder = new StringBuilder(DYNAMIC_VISIT_BODY);

            final Optional<String> primaryVisitorName = visit.getVisitVisitorList().stream().filter(vv ->
                vv.getVisitorContactTypeEnum().equals(VisitorContactTypeEnum.PRIMARY)).map(vv ->
                vv.getVisitor().getFirstName() + " " + vv.getVisitor().getLastName()).findFirst();

            replaceSB(visitStringBuilder, VAR_PRIMARY_VISITOR, primaryVisitorName.orElse(NA));

            final Optional<String> primaryVisitorOrganization = visit.getVisitVisitorList().stream().filter(vv ->
                vv.getVisitorContactTypeEnum().equals(VisitorContactTypeEnum.PRIMARY)).map(vv ->
                vv.getVisitor().getOrganizationName()).findFirst();

            replaceSB(visitStringBuilder, VAR_ORGANIZATION_NAME, primaryVisitorOrganization.orElse(NA));

            replaceSB(visitStringBuilder, VAR_TYPE_OF_VISIT, visit.getTypeOfVisit());

            replaceSB(visitStringBuilder, VAR_TOUR_TYPE, visit.getTourType() != null ? visit.getTourType() : NA);

            final List<String> servicesName = visit.getVisitServiceList().stream().map(vs -> vs.getServiceTemplate().getName()).toList();

            replaceSB(visitStringBuilder, VAR_SERVICES_NAME, CollectionUtils.isNotEmpty(servicesName)
                ? servicesName.toString().replace("[", "").replace("]", "") : NA);

            final Optional<String> rm = visit.getVisitPersonnelList().stream().filter(vp ->
                vp.getRole().getUucode().equals(RELATIONSHIP_MANAGER.name())).map(vp ->
                vp.getPersonnel().getFirstName() + " " + vp.getPersonnel().getLastName()).findFirst();

            replaceSB(visitStringBuilder, VAR_RM, rm.orElse(NA));

            final Optional<String> vc = visit.getVisitPersonnelList().stream().filter(vp ->
                vp.getRole().getUucode().equals(GUEST_VISIT_COORDINATOR.name())).map(vp ->
                vp.getPersonnel().getFirstName() + " " + vp.getPersonnel().getLastName()).findFirst();

            replaceSB(visitStringBuilder, VAR_VC, vc.orElse(NA));

            final List<String> meetingPersonnelList = visit.getVisitServiceList().stream().filter(vs ->
                vs.getMeetingPersonnel() != null).map(vs ->
                vs.getMeetingPersonnel().getFirstName() + " " + vs.getMeetingPersonnel().getLastName()).toList();

            replaceSB(visitStringBuilder, VAR_MULAKAT, CollectionUtils.isNotEmpty(meetingPersonnelList)
                ? meetingPersonnelList.toString().replace("[", "").replace("]", "") : NA);

            replaceSB(visitStringBuilder, VAR_NO_OF_GUESTS, String.valueOf(visit.getTotalVisitors()));

            replaceSB(visitStringBuilder, VAR_VISIT_TIME, visit.getStartDateTime().toLocalTime()
                    .truncatedTo(ChronoUnit.MINUTES).format(DateTimeFormatter.ofPattern("hh:mm a")));

            visitListStringBuilder.append(visitStringBuilder);
        }
        return visitListStringBuilder.toString();
    }

    private static void replaceSB(final StringBuilder sb, final String target, final String replacement) {
        final int index = sb.indexOf(target);
        sb.replace(index, index + target.length(), replacement);
    }

}
