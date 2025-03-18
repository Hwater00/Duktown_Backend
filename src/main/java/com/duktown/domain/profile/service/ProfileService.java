package com.duktown.domain.profile.service;

import com.duktown.domain.chatRoomUser.entity.ChatRoomUserRepository;
import com.duktown.domain.comment.entity.Comment;
import com.duktown.domain.comment.entity.CommentRepository;
import com.duktown.domain.delivery.dto.DeliveryDto;
import com.duktown.domain.delivery.entity.Delivery;
import com.duktown.domain.delivery.entity.DeliveryRepository;
import com.duktown.domain.like.entity.Like;
import com.duktown.domain.like.entity.LikeRepository;
import com.duktown.domain.post.dto.PostDto;
import com.duktown.domain.post.entity.Post;
import com.duktown.domain.post.entity.PostRepository;
import com.duktown.domain.profile.dto.ProfileDto;
import com.duktown.domain.unitUser.entity.UnitUser;
import com.duktown.domain.unitUser.entity.UnitUserRepository;
import com.duktown.domain.user.entity.User;
import com.duktown.domain.user.entity.UserRepository;
import com.duktown.global.exception.CustomException;
import com.duktown.global.type.Category;
import com.duktown.global.type.ChatRoomUserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.duktown.global.exception.CustomErrorType.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final UnitUserRepository unitUserRepository;

    public ProfileDto.ProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        UnitUser unitUser = unitUserRepository.findByUserId(userId).orElseThrow(() -> new CustomException(UNIT_USER_NOT_FOUND));

        return ProfileDto.ProfileResponse.from(user, unitUser.getRoommate(), unitUser.getUnitUserType());
    }

    // 내가 작성한 배달팟 조회
    public DeliveryDto.DeliveryListResponse getMyDeliveries(Long userId, Integer sortBy) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<Delivery> deliveries;

        // deleted = false인 배달팟만 조회됨
        if(sortBy == null || sortBy == 0){  // 최신순 정렬
            deliveries = deliveryRepository.findAllByUserIdSortByCreatedAt(userId);
        } else if (sortBy == 1) {   // 주문시간순 정렬
            deliveries = deliveryRepository.findAllByUserIdSortByOrderTime(userId);
        } else {
            throw new CustomException(INVALID_DELIVERY_SORTBY_VALUE);
        }

        List<DeliveryDto.DeliveryResponse> content = deliveries
                .stream()
                .map(d -> DeliveryDto.DeliveryResponse.from(d, d.getUser().getId().equals(userId),
                        chatRoomUserRepository.countByChatRoomId(d.getChatRoom().getId(), ChatRoomUserType.ACTIVE)))
                .collect(Collectors.toList());

        return new DeliveryDto.DeliveryListResponse(content);
    }

    public DeliveryDto.DeliveryListResponse getMyCommentedDeliveries(Long userId, Integer sortBy) {
        userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<Delivery> deliveries;

        // deleted = false인 배달팟만 조회됨
        if(sortBy == null || sortBy == 0){  // 최신순 정렬
            deliveries = commentRepository.findAllByUserAndDeliverySortByCreatedAt(userId)
                    .stream().map(Comment::getDelivery).distinct().collect(Collectors.toList());
        } else if (sortBy == 1) {   // 주문시간순 정렬
            deliveries = commentRepository.findAllByUserAndDeliverySortByOrderTime(userId)
                    .stream().map(Comment::getDelivery).distinct().collect(Collectors.toList());
        } else {
            throw new CustomException(INVALID_DELIVERY_SORTBY_VALUE);
        }

        List<DeliveryDto.DeliveryResponse> content = deliveries
                .stream()
                .map(d -> DeliveryDto.DeliveryResponse.from(d, d.getUser().getId().equals(userId),
                        chatRoomUserRepository.countByChatRoomId(d.getChatRoom().getId(), ChatRoomUserType.ACTIVE)))
                .distinct()
                .collect(Collectors.toList());

        return new DeliveryDto.DeliveryListResponse(content);
    }

    public PostDto.PostListResponse getMyPosts(Long userId, Integer category) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Category findCategory = Arrays.stream(Category.values())
                .filter(c -> c.getValue() == category)
                .findAny().orElseThrow(() -> new CustomException(INVALID_POST_CATEGORY_VALUE));

        List<Post> posts = postRepository.findAllByUserAndCategory(userId, findCategory);
        List<Like> likes = likeRepository
                .findAllByUserAndPostIn(
                        user.getId(),
                        posts.stream().map(Post::getId)
                                .collect(Collectors.toList())
                );

        List<PostDto.PostResponse> postListResponses = posts.stream()
                .map(p -> new PostDto.PostResponse(p, likes, commentRepository.countByPostId(p.getId()), p.getUser().getId().equals(userId)))
                .collect(Collectors.toList());

        return new PostDto.PostListResponse(postListResponses);
    }

    public PostDto.PostListResponse getMyCommentedPosts(Long userId, Integer category) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Category findCategory = Arrays.stream(Category.values())
                .filter(c -> c.getValue() == category)
                .findAny().orElseThrow(() -> new CustomException(INVALID_POST_CATEGORY_VALUE));

        List<Post> posts = commentRepository.findAllByUserAndPostAndCategory(userId, findCategory)
                .stream().map(Comment::getPost).distinct().collect(Collectors.toList());
        List<Like> likes = likeRepository
                .findAllByUserAndPostIn(
                        user.getId(),
                        posts.stream().map(Post::getId)
                                .collect(Collectors.toList())
                );

        List<PostDto.PostResponse> postListResponses = posts.stream()
                .map(p -> new PostDto.PostResponse(p, likes, commentRepository.countByPostId(p.getId()), p.getUser().getId().equals(userId)))
                .collect(Collectors.toList());

        return new PostDto.PostListResponse(postListResponses);
    }
}
