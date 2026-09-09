package com.wangping.ClaimCenter;

import com.wangping.ClaimCenter.dto.*;
import com.wangping.ClaimCenter.entity.Claim;
import com.wangping.ClaimCenter.entity.ClaimAssignment;
import com.wangping.ClaimCenter.entity.ClaimHistory;
import com.wangping.ClaimCenter.entity.User;
import com.wangping.ClaimCenter.enums.ClaimStatus;
import com.wangping.ClaimCenter.enums.PolicyType;
import com.wangping.ClaimCenter.enums.Role;
import com.wangping.ClaimCenter.repository.ClaimAssignmentRepository;
import com.wangping.ClaimCenter.repository.ClaimHistoryRepository;
import com.wangping.ClaimCenter.repository.ClaimRepository;
import com.wangping.ClaimCenter.repository.UserRepository;
import com.wangping.ClaimCenter.service.impl.ClaimServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import org.springframework.security.access.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClaimServiceImplTest {

    @Mock private ClaimRepository claimRepository;
    @Mock private ClaimHistoryRepository claimHistoryRepository;
    @Mock private ClaimAssignmentRepository claimAssignmentRepository;
    @Mock private UserRepository userRepository;
    @Mock private ApplicationEventPublisher applicationEventPublisher;

    private ClaimServiceImpl claimService;

    private User claimant;
    private User adjuster;
    private User manager;

    @BeforeEach
    void setUp() {
        claimService = new ClaimServiceImpl(claimRepository, claimHistoryRepository,
                claimAssignmentRepository, userRepository, applicationEventPublisher);
        claimant = buildUser(1L, "claimant@test.com", Role.CLAIMANT);
        adjuster = buildUser(2L, "adjuster@test.com", Role.ADJUSTER);
        manager = buildUser(3L, "manager@test.com", Role.MANAGER);
    }

    private User buildUser(Long id, String email, Role role) {
        User u = new User();
        u.setUserId(id);
        u.setEmail(email);
        u.setRole(role);
        return u;
    }

    private Claim buildClaim(Long id, ClaimStatus status, User createdBy) {
        Claim claim = new Claim();
        claim.setId(id);
        claim.setStatus(status);
        claim.setCreatedBy(createdBy);
        claim.setType(PolicyType.CAR);
        claim.setClaimedAmount(BigDecimal.valueOf(1000));
        claim.setPayments(Collections.emptyList());
        return claim;
    }

    private ClaimAssignment buildActiveAssignment(Claim claim, User adjuster) {
        ClaimAssignment assignment = new ClaimAssignment();
        assignment.setClaim(claim);
        assignment.setAdjuster(adjuster);
        assignment.setActive(true);
        return assignment;
    }

    @Test
    void sanityCheckMocksAreInjected() {
        assertNotNull(claimRepository);
    }

    // ---------------------------------------------------------------
    // getClaimDetail - RBAC access checks
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("getClaimDetail")
    class GetClaimDetail {

        @Test
        @DisplayName("Manager can view any claim")
        void managerCanViewAnyClaim() {
            Claim claim = buildClaim(1L, ClaimStatus.SUBMITTED, claimant);
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

            ClaimDetailDto result = claimService.getClaimDetail(1L, manager);

            assertEquals(1L, result.getClaimId());
        }

        @Test
        @DisplayName("Claimant who owns the claim can view it")
        void claimantCanViewOwnClaim() {
            Claim claim = buildClaim(1L, ClaimStatus.SUBMITTED, claimant);
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

            ClaimDetailDto result = claimService.getClaimDetail(1L, claimant);

            assertEquals(1L, result.getClaimId());
        }

        @Test
        @DisplayName("Claimant who does not own the claim is denied")
        void claimantCannotViewOthersClaim() {
            User otherClaimant = buildUser(9L, "other@test.com", Role.CLAIMANT);
            Claim claim = buildClaim(1L, ClaimStatus.SUBMITTED, otherClaimant);
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

            assertThrows(AccessDeniedException.class, () -> claimService.getClaimDetail(1L, claimant));
        }

        @Test
        @DisplayName("Assigned adjuster can view the claim")
        void assignedAdjusterCanView() {
            Claim claim = buildClaim(1L, ClaimStatus.UNDER_REVIEW, claimant);
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
            when(claimAssignmentRepository.findTopByClaimIdAndIsActiveTrueOrderByAssignedAtDesc(1L))
                    .thenReturn(buildActiveAssignment(claim, adjuster));

            ClaimDetailDto result = claimService.getClaimDetail(1L, adjuster);
            assertEquals(1L, result.getClaimId());
        }

        @Test
        @DisplayName("Non-assigned adjuster (e.g. after reassignment) is denied")
        void unassignedAdjusterCannotView() {
            User otherAdjuster = buildUser(4L, "other-adj@test.com",  Role.ADJUSTER);
            Claim claim = buildClaim(1L, ClaimStatus.UNDER_REVIEW, claimant);
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
            when(claimAssignmentRepository.findTopByClaimIdAndIsActiveTrueOrderByAssignedAtDesc(1L))
                    .thenReturn(buildActiveAssignment(claim, otherAdjuster));

            assertThrows(AccessDeniedException.class, () -> claimService.getClaimDetail(1L, adjuster));

        }

        @Test
        @DisplayName("Throws when claim does not exist")
        void throwsWhenClaimNotFound() {
            when(claimRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> claimService.getClaimDetail(99L, manager));
        }
    }

    // ---------------------------------------------------------------
    // createClaim
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("createClaim")
    class CreateClaim {

        @Test
        @DisplayName("Claimant can create a claim, which starts as SUBMITTED")
        void claimantCanCreateClaim() {
            CreateClaimRequestDto request = new CreateClaimRequestDto();
            request.setTitle("Fender bender");
            request.setDescription("Rear-ended at a red light");
            request.setType(PolicyType.CAR);
            request.setClaimedAmount(BigDecimal.valueOf(1000));

            Claim saved = buildClaim(1L, ClaimStatus.SUBMITTED, claimant);
            when(claimRepository.save(any(Claim.class))).thenReturn(saved);

            ClaimDetailDto result = claimService.createClaim(request, claimant);
            assertEquals(1L, result.getClaimId());

            ArgumentCaptor<Claim> claimCaptor = ArgumentCaptor.forClass(Claim.class);
            verify(claimRepository).save(claimCaptor.capture());
            assertEquals(ClaimStatus.SUBMITTED, claimCaptor.getValue().getStatus());
            assertEquals(claimant, claimCaptor.getValue().getCreatedBy());

            verify(claimHistoryRepository).save(any(ClaimHistory.class));
        }

        @ParameterizedTest
        @EnumSource(value = Role.class, names = {"ADJUSTER", "MANAGER"})
        @DisplayName("Non-claimants cannot create a claim")
        void nonClaimantCannotCreateClaim(Role role) {
            User nonClaimant = buildUser(5L, "x@test.com", role);
            CreateClaimRequestDto request = new CreateClaimRequestDto();

            assertThrows(AccessDeniedException.class, () -> claimService.createClaim(request, nonClaimant));

            verify(claimRepository, never()).save(any());
        }
    }

    // ---------------------------------------------------------------
    // assignClaim
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("assignClaim")
    class AssignClaim {

        @Test
        @DisplayName("Manager can assign an adjuster, claim moves to UNDER_REVIEW")
        void managerCanAssignClaim() {
            Claim claim = buildClaim(1L, ClaimStatus.SUBMITTED, claimant);
            AssignClaimRequestDto request = new AssignClaimRequestDto();
            request.setAdjusterId(2L);

//            adjuster.setAdjuster(true);
            when(userRepository.findById(2L)).thenReturn(Optional.of(adjuster));
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
            when(claimAssignmentRepository.findByClaimIdAndIsActiveTrue(1L))
                    .thenReturn(Collections.emptyList());

            AssignClaimResponseDto result = claimService.assignClaim(1L, request, manager);

            assertTrue(result.isAssigned());
            assertEquals(ClaimStatus.UNDER_REVIEW, claim.getStatus());
            verify(claimAssignmentRepository).save(any(ClaimAssignment.class));
            verify(claimHistoryRepository).save(any(ClaimHistory.class));

        }

        @Test
        @DisplayName("Non-managers cannot assign claims")
        void nonManagerCannotAssign() {
            AssignClaimRequestDto request = new AssignClaimRequestDto();
            request.setAdjusterId(2L);

            assertThrows(RuntimeException.class, () -> claimService.assignClaim(1L, request, claimant));

            verify(claimRepository, never()).findById(any());

        }

        @Test
        @DisplayName("Cannot reassign a claim that is already APPROVED")
        void cannotReassignApprovedClaim() {
            Claim claim = buildClaim(1L, ClaimStatus.APPROVED, claimant);
            AssignClaimRequestDto request = new AssignClaimRequestDto();
            request.setAdjusterId(2L);

            when(userRepository.findById(2L)).thenReturn(Optional.of(adjuster));
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

            assertThrows(IllegalStateException.class, () -> claimService.assignClaim(1L, request, manager));
        }

        @Test
        @DisplayName("Cannot assign a non-adjuster user")
        void cannotAssignNonAdjusterUser() {
            AssignClaimRequestDto request = new AssignClaimRequestDto();
            request.setAdjusterId(3L);
            when(userRepository.findById(3L)).thenReturn(Optional.of(adjuster));

            assertThrows(RuntimeException.class, () -> claimService.assignClaim(1L, request, manager));
        }
    }

    // ---------------------------------------------------------------
    // approveClaim - includes payout calculation
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("approveClaim")
    class ApproveClaim {

        @Test
        @DisplayName("Assigned adjuster can approve; payout is calculated correctly for CAR policy")
        void assignedAdjusterCanApprove() {
            Claim claim = buildClaim(1L, ClaimStatus.APPROVED, claimant);
            claim.setType(PolicyType.CAR);
            claim.setClaimedAmount(BigDecimal.valueOf(1000)); // (1000-100)*0.5 = 450.00

            when(claimAssignmentRepository.findTopByClaimIdAndIsActiveTrueOrderByAssignedAtDesc(1L))
                    .thenReturn(buildActiveAssignment(claim, adjuster));
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

            ClaimDetailDto result = claimService.approveClaim(1L, adjuster);

            assertEquals(ClaimStatus.APPROVED, result.getStatus());
            assertEquals(0, BigDecimal.valueOf(450.00).compareTo(claim.getPayoutAmount()));
            verify(applicationEventPublisher).publishEvent(any(ClaimApprovedEvent.class));
            verify(claimHistoryRepository).save(any(ClaimHistory.class));

        }

        @Test
        @DisplayName("Payout never goes negative when claimed amount is below the deductible")
        void payoutFloorsAtZeroBelowDeductible() {
            Claim claim = buildClaim(1L, ClaimStatus.APPROVED, claimant);
            claim.setType(PolicyType.PET);
            claim.setClaimedAmount(BigDecimal.valueOf(10)); // deductible is 50

            when(claimAssignmentRepository.findTopByClaimIdAndIsActiveTrueOrderByAssignedAtDesc(1L))
                    .thenReturn(buildActiveAssignment(claim, adjuster));
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

            claimService.approveClaim(1L, adjuster);

            assertEquals(0, BigDecimal.valueOf(0.00).compareTo(claim.getPayoutAmount()));
        }

        @Test
        @DisplayName("Non-adjusters cannot approve claims")
        void nonAdjustersCannotApprove() {
            assertThrows(RuntimeException.class, () -> claimService.approveClaim(1L, manager));
        }

        @Test
        @DisplayName("Adjuster not currently assigned to the claim is denied")
        void unassignedAdjusterCannotApprove() {
            User otherAdjuster = buildUser(4L, "other@test.com", Role.ADJUSTER);
            Claim claim = buildClaim(1L, ClaimStatus.UNDER_REVIEW, claimant);
            when(claimAssignmentRepository.findTopByClaimIdAndIsActiveTrueOrderByAssignedAtDesc(1L))
                    .thenReturn(buildActiveAssignment(claim, otherAdjuster));

            assertThrows(RuntimeException.class, () -> claimService.approveClaim(1L, adjuster));

        }

        @Test
        @DisplayName("Cannot approve a claim that's already been overridden")
        void cannotApproveOverriddenClaim() {
            Claim claim = buildClaim(1L, ClaimStatus.APPROVED, claimant);
            when(claimAssignmentRepository.findTopByClaimIdAndIsActiveTrueOrderByAssignedAtDesc(1L))
                    .thenReturn(buildActiveAssignment(claim, adjuster));
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

            assertThrows(IllegalStateException.class, () -> claimService.approveClaim(1L, adjuster));

            verify(applicationEventPublisher, never()).publishEvent(any());

        }

        @Test
        @DisplayName("Cannot approve a claim that's already been rejected")
        void cannotApproveRejectedClaim() {
            Claim claim = buildClaim(1L, ClaimStatus.REJECTED, claimant);
            when(claimAssignmentRepository.findTopByClaimIdAndIsActiveTrueOrderByAssignedAtDesc(1L))
                    .thenReturn(buildActiveAssignment(claim, adjuster));
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

            assertThrows(IllegalStateException.class, () -> claimService.approveClaim(1L, adjuster));
        }
    }
    // ---------------------------------------------------------------
    // rejectClaim
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("rejectClaim")
    class RejectClaim {
        @Test
        @DisplayName("Assigned adjuster can reject an under-review claim")
        void assignedAdjusterCanReject() {
            Claim claim = buildClaim(1L, ClaimStatus.UNDER_REVIEW, claimant);
            when(claimAssignmentRepository.findTopByClaimIdAndIsActiveTrueOrderByAssignedAtDesc(1L))
                    .thenReturn(buildActiveAssignment(claim, adjuster));
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

            claimService.rejectClaim(1L, adjuster);

            assertEquals(ClaimStatus.REJECTED, claim.getStatus());
            assertNotNull(claim.getClosedAt());
            verify(claimHistoryRepository).save(any(ClaimHistory.class));
        }

        @Test
        @DisplayName("Cannot rejet an already-approve claim")
        void cannotRejectApprovedClaim() {
            Claim claim = buildClaim(1L, ClaimStatus.REJECTED, claimant);
            when(claimAssignmentRepository.findTopByClaimIdAndIsActiveTrueOrderByAssignedAtDesc(1L))
                    .thenReturn(buildActiveAssignment(claim, adjuster));
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

            assertThrows(IllegalStateException.class, () -> claimService.rejectClaim(1L, adjuster));
        }
    }

    // ---------------------------------------------------------------
    // overrideClaim
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("overrideClaim")
    class OverrideClaim {

        @Test
        @DisplayName("Manager can override to approved, triggering payout + payment event")
        void managerCanOverrideToApproved() {
            Claim claim = buildClaim(1L, ClaimStatus.APPROVED, claimant);
            claim.setType(PolicyType.CAR);
            claim.setClaimedAmount(BigDecimal.valueOf(1000));
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
            when(claimRepository.save(any(Claim.class))).thenReturn(claim);

            claimService.overrideClaim(1L, true, manager);

            assertEquals(ClaimStatus.OVERRIDDEN_APPROVED, claim.getStatus());
            verify(applicationEventPublisher).publishEvent(any(ClaimApprovedEvent.class));

        }

        @Test
        @DisplayName("Manager can override to rejected, without a payment event")
        void managerCanOverrideToRejected() {
            Claim claim = buildClaim(1L, ClaimStatus.APPROVED, claimant);
            when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
            when(claimRepository.save(any(Claim.class))).thenReturn(claim);

            claimService.overrideClaim(1L, false, manager);

            assertEquals(ClaimStatus.OVERRIDDEN_REJECTED, claim.getStatus());
            verify(applicationEventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Non-managers cannot override a claim")
        void nonManagerCannotOverride() {
            assertThrows(IllegalStateException.class,
                    () -> claimService.overrideClaim(1L, true, adjuster));
            verify(claimRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Throws when the claim to override does not exist")
        void throwsWhenClaimMissing() {
            when(claimRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> claimService.overrideClaim(99L, true, adjuster));
        }
    }

    // ---------------------------------------------------------------
    // getAdjusters
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("getAdjusters")
    class GetAdjusters {

        @Test
        @DisplayName("Manager can list al adjusters")
        void managerCanListAdjusters() {
            when(userRepository.findAllByRole(Role.ADJUSTER)).thenReturn(List.of(adjuster));

            List<AdjusterDto> result = claimService.getAdjusters(manager);

            assertEquals(1, result.size());
            assertEquals("ADJUSTER", result.get(0).getRole());
        }

        @Test
        @DisplayName("Non-manager cannot list adjusters")
        void nonManagerCannotListAdjusters() {
            assertThrows(RuntimeException.class, () -> claimService.getAdjusters(claimant));

            verify(userRepository, never()).findAllByRole(any());
        }
    }
}
