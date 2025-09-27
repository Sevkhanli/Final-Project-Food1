package az.edu.itbrains.food.services;

import az.edu.itbrains.food.DTOs.request.ContactMessageRequestDTO;
import az.edu.itbrains.food.DTOs.response.ContactMessageResponseDTO;

import java.util.List;

public interface IContactMessageService {
    ContactMessageResponseDTO createContactMessage(ContactMessageRequestDTO contactMessageRequestDTO);
    ContactMessageResponseDTO updateContactMessage( Long id,ContactMessageRequestDTO contactMessageRequestDTO);
    ContactMessageResponseDTO getContactMessageById(Long id);
    void deleteContactMessage(Long id);
    List<ContactMessageResponseDTO> getAllCategory();
    ContactMessageResponseDTO getContactMessage(ContactMessageRequestDTO contactMessageRequestDTO);


}
